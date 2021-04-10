package com.github.taxbeans.crypto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.taxbeans.crypto.model.KrakenTrade;
import com.github.taxbeans.model.commodity.Commodity;
import com.github.taxbeans.model.commodity.CommodityAmount;

public class MarginPosition {
	
	final static Logger logger = LoggerFactory.getLogger(KrakenTradeParser.class);
	
	public MarginPosition() {
		super();
		logger.debug(this.toString());
	}

	private List<KrakenTrade> openingTrades = new ArrayList<KrakenTrade>();
	
	private List<KrakenTrade> closingTrades = new ArrayList<KrakenTrade>();
	
	private KrakenTrade totalPosition = new KrakenTrade();
	
	private CommodityAmount rolloverCost = CommodityAmount.commodityAmount()
			.withCommodity(Commodity.commodity().withSymbol("USD").build())
			.withAmount(BigDecimal.ZERO).build();
	
	public void addOpeningTrade(KrakenTrade trade) {
		logger.error(this.toString());
		openingTrades.add(trade);
		totalPosition.setCost(totalPosition.getCost().add(trade.getCost()));
		totalPosition.setMargin(totalPosition.getMargin().add(trade.getMargin()));
		totalPosition.recalculateLeverage();
		logger.debug(this.toString());
	}
	
	public void addClosingTrade(KrakenTrade trade) {
		logger.error(this.toString());
		closingTrades.add(trade);
		totalPosition.setCost(totalPosition.getCost().subtract(trade.getCost()));
		totalPosition.setMargin(totalPosition.getMargin().subtract(trade.getMargin()));
		totalPosition.recalculateLeverage();
		logger.error(this.toString());
		if (totalPosition.getCost().compareTo(BigDecimal.ZERO) <= 0) {
			logger.info("Position closed");
		}
	}

	@Override
	public String toString() {
		return String.format("MarginPosition [rolloverCost=%s, openingTrades=%s, closingTrades=%s, totalPosition=%s]",
				rolloverCost, openingTrades, closingTrades, totalPosition);
	}

	public KrakenTrade getTotalPosition() {
		logger.info(this.toString());
		return totalPosition;
	}

	public CommodityAmount getRolloverCost() {
		logger.info(this.toString());
		return this.rolloverCost;
	}

	public void addRolloverCost(CommodityAmount rolloverCost) {
		logger.info(this.toString());
		this.rolloverCost.add(rolloverCost);
	}

}
