package com.homeware.pos.service;

import java.nio.charset.StandardCharsets;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;

import org.springframework.stereotype.Service;

import com.homeware.pos.model.Order;
import com.homeware.pos.model.OrderItem;

@Service
public class ReceiptPrinterService {

    public void printReceiptAndOpenDrawer(Order order, boolean isCashPayment) {
        try {
            PrintService defaultService = PrintServiceLookup.lookupDefaultPrintService();
            if (defaultService == null) {
                System.out.println("Warning: No default thermal printer found. Skipping physical print.");
                return;
            }

            StringBuilder sb = new StringBuilder();

            // ESC @ - Initialize printer
            sb.append((char) 27).append((char) 64);

            // ESC a 1 - Center Alignment
            sb.append((char) 27).append((char) 97).append((char) 1);
            sb.append("HOMEWARE POS SYSTEM\n");
            sb.append("123 Main Street, Colombo\n");
            sb.append("--------------------------------\n");
            sb.append("Receipt #: ").append(order.getReceiptNumber()).append("\n");
            sb.append("Date: ").append(order.getOrderTime()).append("\n");
            sb.append("--------------------------------\n");

            // ESC a 0 - Left Alignment for items
            sb.append((char) 27).append((char) 97).append((char) 0);
            for (OrderItem item : order.getItems()) {
                String itemName = item.getProductVariant().getProduct().getName();
                String variantInfo = (item.getProductVariant().getSize() != null ? item.getProductVariant().getSize()
                        : "") +
                        " " + (item.getProductVariant().getColor() != null ? item.getProductVariant().getColor() : "");

                sb.append(itemName).append(" (").append(variantInfo.trim()).append(")\n");
                sb.append("  ").append(item.getQuantity()).append(" x Rs. ").append(item.getUnitPrice())
                        .append(" = Rs. ").append(item.getLineTotal()).append("\n");
            }

            sb.append("--------------------------------\n");
            // Right / Center align totals
            sb.append((char) 27).append((char) 97).append((char) 2);
            sb.append("Subtotal: Rs. ").append(order.getSubtotal()).append("\n");
            sb.append("TOTAL: Rs. ").append(order.getTotalAmount()).append("\n");
            sb.append("--------------------------------\n");

            sb.append((char) 27).append((char) 97).append((char) 1);
            sb.append("Thank you for shopping with us!\n\n\n");

            // GS V 66 0 - Cut paper feed
            sb.append((char) 29).append((char) 86).append((char) 66).append((char) 0);

            byte[] printData = sb.toString().getBytes(StandardCharsets.US_ASCII);
            DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;

            // Send Print Job
            DocPrintJob printJob = defaultService.createPrintJob();
            Doc doc = new SimpleDoc(printData, flavor, null);
            printJob.print(doc, null);

            // Trigger Cash Drawer Kick if payment was Cash: ESC p 0 25 250
            if (isCashPayment) {
                byte[] drawerCommand = new byte[] { 27, 112, 0, 25, (byte) 250 };
                DocPrintJob drawerJob = defaultService.createPrintJob();
                Doc drawerDoc = new SimpleDoc(drawerCommand, flavor, null);
                drawerJob.print(drawerDoc, null);
            }

            System.out.println("Receipt printed and drawer triggered successfully.");

        } catch (Exception e) {
            System.err.println("Thermal printing failed: " + e.getMessage());
        }
    }
}