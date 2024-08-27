package com.batch.batchproject.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import com.batch.batchproject.model.Product;

public class CustumItemProcessor implements ItemProcessor<Product, Product>{

	 private Logger logger= LoggerFactory.getLogger(CustumItemProcessor.class);
		
    /**
     * Processes a Product item by calculating its discounted price.
     *
     * @param item the Product item to process
     * @return the processed Product item with updated discounted price
     * @throws Exception if any error occurs during processing
     */
    @Override
    public Product process(Product item) throws Exception {
        if (item == null) {
        	 logger.warn("Received null Product item. Skipping processing.");
            return null;
        }

        try {
           
            // Parse discount and price values
            int discountPer = parseInt(item.getDiscount());
            double originalPrice = parseDouble(item.getPrice());

            // Ensure discountPer is within a valid range
            if (discountPer < 0 || discountPer > 100) {
                throw new IllegalArgumentException("Discount percentage must be between 0 and 100.");
            }

            // Calculate the final price after applying the discount
            double discount = (discountPer / 100.0) * originalPrice; // Use 100.0 for floating-point division
            double finalPrice = originalPrice - discount;

            // Update the item with the calculated discounted price
            item.setDiscountedPrice(String.format("%.2f", finalPrice)); // Format to 2 decimal places
        }catch (NumberFormatException ex) {
           
            logger.error("Error parsing number: {}", ex.getMessage(), ex);
        } catch (IllegalArgumentException ex) {
           
            logger.error(ex.getMessage());
        }
        return item;
    }

    /**
     * Utility method to safely parse an integer from a string.
     *
     * @param value the string to parse
     * @return the parsed integer or 0 if parsing fails
     */
    private int parseInt(String value) {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            // Log and return default value (0) if parsing fails
        	 logger.error("Invalid integer value: {}", value);
            return 0;
        }
    }

    private double parseDouble(String value) {
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            // Log and return default value (0.0) if parsing fails
        	 logger.error("Invalid double value: {}", value);
            return 0.0;
        }
	}

}
