package com.github.taxbeans.forms.nz;

import java.util.Iterator;

import org.apache.commons.beanutils.BeanMap;

public class BeanUtils {
	
	public static void process(Object bean) {

        BeanMap map = new BeanMap(bean);
        
        //todo obtain field mapper through annotation
        //Iterate through keys
        Iterator<String> it = map.keyIterator();
        while (it.hasNext()) {
            String key = it.next();
			System.out.println(key);
            String fieldName = IR3FieldMapper.getFieldName(IR3Fields.valueOf(key), 2017);
            System.out.println("***: " + fieldName);
        }
        
        System.out.println();
        
        //Print out both keys and values
        it = map.keyIterator();
        while (it.hasNext()) {
            String key = it.next();
            System.out.println(key + ": " + map.get(key));
        }
	}
	
	public static void main(String[] args) {
		BeanUtils.process(new IR3FormBean());
	}

}
