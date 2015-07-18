

package com.cilogi.aws.cart;

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
    private static final String ITEM_ID = "1847972314";

    public static void main(String[] args) {

        ItemLookup item = new ItemLookup(ITEM_ID);
        System.out.println("Title is " + item.getTitle());
        System.out.println("Description is " + item.getDescription());
        System.out.println("Image is " + item.getImage());
        System.out.println("Price is " + item.getListPrice());
    }
}
