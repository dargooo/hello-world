import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReturnController implements ActionListener {

    ReturnView rView;
    RemoteDataAccess db;
    DecimalFormat df = new DecimalFormat(".##");
    public Double subTotal = 0.0, taxTotal = 0.0, costToal = 0.0;
    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

    public ReturnController(ReturnView view, RemoteDataAccess da) {
        rView = view;
        rView.btnReturn.addActionListener(this);
        rView.btnCancel.addActionListener(this);
        rView.btnSave.addActionListener(this);
        db = da;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == rView.btnCancel) {
            Application.cashierView.show();
            Application.returnView.setVisible(false);
            subTotal = 0.0;
            taxTotal = 0.0;
            costToal = 0.0;
            Application.returnView.clear();
        }
        if (e.getSource() == rView.btnSave) {
            loadOrderline();
        }
        if (e.getSource() == rView.btnReturn) {
            createReturn();
        }
    }

    private void createReturn() {
        ReturnModel returnModel = new ReturnModel();
        returnModel.returnID = -1;
        returnModel.customer = rView.txtCustomer.getText();
        String name;
        if (returnModel.customer == null || returnModel.customer.equals("")) {
            returnModel.customer = "anonymous";
            name = "Anonymous";
        }
        else {
            name = db.findCustomer(returnModel.customer).name;
        }
        returnModel.subTotal = Double.parseDouble(df.format(subTotal));
        returnModel.tax = Double.parseDouble(df.format(taxTotal));
        returnModel.total = Double.parseDouble(df.format(costToal));
        Date date = new Date();
        returnModel.date = dateFormat.format(date);
        db.saveReturn(returnModel);
        JOptionPane.showMessageDialog(null, "" + name + " return products successfully!\n"
                + "Total Amount: $" + returnModel.total);
    }

    private void loadOrderline() {
        try {
            int barcode = Integer.parseInt(rView.txtBarcode.getText());
            ProductModel p = db.findProduct(barcode);
            if (p == null) {
                JOptionPane.showMessageDialog(null, "Where did you get that barcode?");
                return;
            }
            else {
                double q = Double.parseDouble(rView.txtQuantity.getText());
                rView.addTxt(rView.c1, String.valueOf(p.productID));
                rView.addTxt(rView.c2, p.name);
                rView.addTxt(rView.c3, String.valueOf(q));
                double price = p.price * q, tax = price * 0.09, total = tax + price;
                subTotal += price;
                taxTotal += tax;
                costToal += total;
                rView.addTxt(rView.c4, String.valueOf(df.format(price)));
                rView.addTxt(rView.c5, String.valueOf(df.format(tax)));
                rView.addTxt(rView.c6, String.valueOf(df.format(total)));

                //update product
                p.quantity += q;
                db.saveProduct(p);
            }
        }
        catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Invalid format for ProductID");
            ex.printStackTrace();
        }
    }
}
