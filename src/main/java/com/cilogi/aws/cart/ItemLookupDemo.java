

package com.cilogi.aws.cart;

import com.cilogi.ds.guide.shop.Sku;
import org.w3c.dom.Document;

import java.util.logging.Logger;

/*
 * This class shows how to make a simple authenticated ItemLookup call to the
 * Amazon Product Advertising API.
 * 
 * See the README.html that came with this sample for instructions on
 * configuring and running the sample.
 */
public class ItemLookupDemo {
    @SuppressWarnings({"unused"})
    static final Logger LOG = Logger.getLogger(ItemLookupDemo.class.getName());
    private static final String ITEM_ID = "B00J8O28FY";

    public static void main(String[] args) {

        ItemLookup item = new ItemLookup(ITEM_ID);
        Document doc = item.getDoc();
        BasicOp.printDocument(doc, System.out);
        Sku sku = item.getInfo();
        System.out.println("sku is " + sku.toJsonString());
    }
}
