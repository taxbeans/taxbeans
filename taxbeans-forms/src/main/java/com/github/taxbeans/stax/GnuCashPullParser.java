package com.github.taxbeans.stax;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.math.MathContext;
import java.nio.charset.Charset;
import java.nio.charset.MalformedInputException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.taxbeans.cache.FileCache;
import com.github.taxbeans.forms.utils.TaxRegion;
import com.github.taxbeans.model.Account;
import com.github.taxbeans.model.AccountClassification;
import com.github.taxbeans.model.AccountEntry;
import com.github.taxbeans.model.Ledger;
import com.github.taxbeans.model.Transaction;

public class GnuCashPullParser {

	static final Logger logger = LoggerFactory.getLogger(GnuCashPullParser.class);

	private Map<String, String> guidToAccountNameMap = new HashMap<String, String>();

	private String filename;

	private boolean cacheData = true;

	/**
	 * Timezone for interpreting gnucash dates, usually the system timezone
	 */
	private ZoneId zoneId;

	private static Map<String, Ledger> map = new ConcurrentHashMap<String, Ledger>();

	public GnuCashPullParser(String filename, ZoneId zoneId) {
		super();
		this.filename = filename;
		this.zoneId = zoneId;
	}

	public GnuCashPullParser(String filename) {
		this(filename, TaxRegion.getDefault().getZone());
	}

	public List<AccountEntry> parseSplits(XMLStreamReader streamReader, Transaction transaction) throws XMLStreamException, ParseException {
		List<AccountEntry> transactionSplits = new ArrayList<AccountEntry>();
		outer:
			while (streamReader.hasNext()) {
				int eventType = streamReader.next();
				if (eventType == XMLStreamReader.END_ELEMENT && "splits".equals(streamReader.getLocalName()))
					return transactionSplits;
				if(eventType == XMLStreamReader.START_ELEMENT){
					if ("split".equals(streamReader.getLocalName())) {
						AccountEntry transactionSplit = new AccountEntry();
						transactionSplit.setTransaction(transaction);
						transactionSplits.add(transactionSplit);
						while (streamReader.hasNext()) {
							eventType = streamReader.next();
							if (eventType == XMLStreamReader.END_ELEMENT && "split".equals(streamReader.getLocalName()))
								continue outer;
							if(eventType == XMLStreamReader.START_ELEMENT && "value".equals(streamReader.getLocalName())) {
								//logger.debug("value/amount namespace = " + streamReader.getNamespaceURI());
								if (!"http://www.gnucash.org/XML/split".equals(streamReader.getNamespaceURI()))
									continue;
								eventType = streamReader.next();
								if (eventType == XMLStreamReader.END_ELEMENT)
									continue;  //should be characters perhaps?
								String text = streamReader.getText();
								//logger.debug("text amount = " + text);
								if (text.endsWith("/100")) {
									text = text.substring(0,text.length()-4);
									BigDecimal amount = new BigDecimal(text).divide(new BigDecimal("100"), MathContext.DECIMAL128);
									//logger.debug("amount = " + amount);
									transactionSplit.setAmount(amount);
								} else {
									//logger.debug("unrecognized amount string " + text);
									transactionSplit.setAmount(BigDecimal.ZERO);
								}
								continue;
							}
							if (eventType == XMLStreamReader.START_ELEMENT && "account".equals(streamReader.getLocalName())) {
								streamReader.next();
								String text = streamReader.getText();
								Account account = Account.createFromGUID(text);
								String argName = this.guidToAccountNameMap.get(text);
								if (argName == null)
									throw new IllegalStateException("not mapped");
								account.setName(argName);
								transactionSplit.setAccount(account);
								continue;
							}
						}
					}

				}
			}
		return transactionSplits;
	}

	public void parseTransaction(XMLStreamReader streamReader, Transaction transaction) throws XMLStreamException, ParseException {
		while(streamReader.hasNext()){
			int eventType = streamReader.next();
			if(eventType == XMLStreamReader.START_ELEMENT){
				//logger.debug(streamReader.getLocalName());
				if ("date-posted".equals(streamReader.getLocalName())) {
					while(streamReader.hasNext()){
						eventType = streamReader.next();
						if(eventType == XMLStreamReader.START_ELEMENT){
							//logger.debug("local = " + streamReader.getLocalName());
							streamReader.next();
							String datePosted = streamReader.getText();
							SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							Date parse = dateFormat.parse(datePosted);
							transaction.setDate(ZonedDateTime.ofInstant(parse.toInstant(), zoneId));
							//logger.debug("parsed date = " + parse);
							//logger.debug("datePosted = " + datePosted);
							break;
						}
					}
				} else if ("date-entered".equals(streamReader.getLocalName())) {
					while(streamReader.hasNext()){
						eventType = streamReader.next();
						if (eventType == XMLStreamReader.START_ELEMENT) {
							//logger.debug("local = " + streamReader.getLocalName());
							streamReader.next();
							String dateEntered = streamReader.getText();
							SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							Date parse = dateFormat.parse(dateEntered);
							transaction.setDateEntered(ZonedDateTime.ofInstant(parse.toInstant(), zoneId));
							//logger.debug("parsed date = " + parse);
							//logger.debug("datePosted = " + datePosted);
							break;
						}
					}
				} else if ("num".equals(streamReader.getLocalName())) {
					eventType = streamReader.next();
					if (eventType != XMLStreamReader.END_ELEMENT){
						transaction.setNum(streamReader.getText());
					}
				} else if ("description".equals(streamReader.getLocalName())) {
					eventType = streamReader.next();
					if (eventType != XMLStreamReader.END_ELEMENT){
						transaction.setDescription(streamReader.getText());
					}
				} else if ("splits".equals(streamReader.getLocalName())) {
					//logger.debug("found splits");
					transaction.setTransactionSplits(parseSplits(streamReader, transaction));
				} else {
					//logger.debug("tx localname = " + streamReader.getLocalName());
				}
			}

			if(eventType == XMLStreamReader.END_ELEMENT){
				//logger.debug(streamReader.getLocalName());
				if ("transaction".equals(streamReader.getLocalName())) {
					return;
				}
			}
		}
	}

	public void parseAccount(XMLStreamReader streamReader, Account account) throws XMLStreamException, ParseException {
		while(streamReader.hasNext()){
			int eventType = streamReader.next();
			if (eventType == XMLStreamReader.END_ELEMENT && "account".equals(streamReader.getLocalName()))
				return;
			if(eventType == XMLStreamReader.START_ELEMENT){
				if ("name".equals(streamReader.getLocalName())) {
					//logger.debug("found account name");
					eventType = streamReader.next();
					if (eventType == XMLStreamReader.END_ELEMENT) {
						logger.debug(streamReader.getLocalName());
						logger.debug(streamReader.getNamespaceURI());
						account.setName("EMPTY");
					} else
						account.setName(streamReader.getText());
				} else if ("id".equals(streamReader.getLocalName())) {
					//logger.debug("id namespace = " + streamReader.getNamespaceURI());
					if (!"http://www.gnucash.org/XML/act".equals(streamReader.getNamespaceURI()))
						continue;
					//logger.debug("found account id");
					streamReader.next();
					account.setGuid(streamReader.getText());
				} else if ("type".equals(streamReader.getLocalName())) {
					//logger.debug("found account type");
					streamReader.next();
					account.setAccountClassification(AccountClassification.fromString(streamReader.getText()));
				} else if ("code".equals(streamReader.getLocalName())) {
					streamReader.next();
					account.setCode(streamReader.getText());
				} else {
					//logger.warn("account localname = " + streamReader.getLocalName());
				}
			}

			if (eventType == XMLStreamReader.END_ELEMENT && "account".equals(streamReader.getLocalName())) {
				return;
			}
		}
	}


	public Ledger parse() throws Exception {
		String pathName = "target/classes/" + filename;
		if (cacheData ) {
			if (map.containsKey(pathName)) {
				return map.get(pathName);
			}
		}
		File fileNameResult;
		File fileNameWithTargetClassesPrepended = new File(pathName);
		if (!fileNameWithTargetClassesPrepended.exists()) {
			File fileCache = FileCache.getCacheLocation();
			File fileNameWithCachePathPrepended = new File(fileCache, filename);
			if (!fileNameWithCachePathPrepended.exists()) {
				File relativeFileName = new File(filename);
				if (!relativeFileName.exists()) {
					String workingDirectory = System.getProperty("user.dir");
					workingDirectory += "/../nzsd/nzsd-accounts/target/classes/" + filename;
					File workingDirectoryFile = new File(workingDirectory);
					if (!workingDirectoryFile.exists()) {
						throw new RuntimeException(String.format(
								"Neither %s, %s nor %s exists, and the current working directory is %s",
								fileNameWithTargetClassesPrepended.toString(),
								fileNameWithCachePathPrepended.toString(),
								relativeFileName,
								workingDirectoryFile));
					} else {
						fileNameResult = workingDirectoryFile;
					}
				} else {
					fileNameResult = relativeFileName;
				}
			} else {
				fileNameResult = fileNameWithCachePathPrepended;
			}
		} else {
			fileNameResult = fileNameWithTargetClassesPrepended;
		}
		/*
		 * For some reason the prolog is causing a parsing error, could try removing
		 * BOMs using: xml = xml.trim().replaceFirst("^([\\W]+)<","<");
		 */
		logger.info("Removing prolog from : " + fileNameResult.getCanonicalPath());
		boolean removeFirstLine = true;
		if (removeFirstLine) {
			List<String> lines = null;
			try {
				lines = Files.readAllLines(Paths.get(fileNameResult.getPath()),
						Charset.forName("UTF-8"));
			} catch (MalformedInputException e) {
				lines = Files.readAllLines(Paths.get(fileNameResult.getPath()),
						Charset.forName("ISO-8859-1"));
			}
			File fout = File.createTempFile("tmp", "guncash");
			FileOutputStream fos = new FileOutputStream(fout);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
			int count = 0;
			for (String line : lines) {
				if (count++ == 0) {
					continue;
				}
				if (line.startsWith(">>>>>>>")) {
					/*
					 * not sure why this is needed at all
					 */
					continue;
				}
				bw.write(line);
				bw.newLine();
			}
			bw.close();
			fileNameResult = fout;
		}
		logger.info("About to parse: " + fileNameResult.getCanonicalPath());
		XMLStreamReader streamReader = XMLInputFactory.newFactory().createXMLStreamReader(
				new FileReader(fileNameResult));
		//int transactionCount = 0;
		//int accountCount = 0;
		List<Transaction> transactions = new ArrayList<Transaction>();
		List<Account> accounts = new ArrayList<Account>();
		while(streamReader.hasNext()){
			int eventType = streamReader.next();
			if(eventType == XMLStreamReader.START_ELEMENT){
				if ("transaction".equals(streamReader.getLocalName())) {
					Transaction transaction = new Transaction();
					transactions.add(transaction);
					parseTransaction(streamReader, transaction);
					//transactionCount++;
				} else if ("account".equals(streamReader.getLocalName())) {
					if (!"http://www.gnucash.org/XML/gnc".equals(streamReader.getNamespaceURI()))
						continue;
					Account account = new Account();
					accounts.add(account);
					parseAccount(streamReader, account);
					guidToAccountNameMap.put(account.getGuid(), account.getName());
					//accountCount++;

				}
			}
		}
		//logger.debug("found " + transactionCount + " transactions");
		//logger.debug("found " + accountCount + " accounts");
		Ledger gnuCashData = new Ledger();
		gnuCashData.setAccounts(accounts);
		gnuCashData.setTransactions(transactions);
		if (cacheData) {
			map.put(pathName, gnuCashData);
		}
		return gnuCashData;
	}

	public static void main(String[] args) throws Exception {
		new GnuCashPullParser("gnucash.xml.gnucash", ZoneId.systemDefault()).parse();
	}

}
