package org.semux.gui.panel;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import org.semux.Config;
import org.semux.core.Unit;
import org.semux.core.Wallet;
import org.semux.crypto.EdDSA;
import org.semux.gui.Action;

public class SendPanel extends JPanel implements ActionListener {

    private static final long serialVersionUID = 1L;

    private JComboBox<String> payFrom;
    private JTextField payTo;
    private JTextField payAmount;
    private JTextField payFee;

    public SendPanel() {
        setBorder(new LineBorder(Color.LIGHT_GRAY));

        JLabel lblFrom = new JLabel("From:");
        lblFrom.setHorizontalAlignment(SwingConstants.RIGHT);

        JLabel lblTo = new JLabel("To:");
        lblTo.setHorizontalAlignment(SwingConstants.RIGHT);

        payTo = new JTextField();
        payTo.setColumns(24);

        JLabel lblAmount = new JLabel("Amount:");
        lblAmount.setHorizontalAlignment(SwingConstants.RIGHT);

        payAmount = new JTextField();
        payAmount.setColumns(10);

        JLabel lblFee = new JLabel("Fee:");
        lblFee.setHorizontalAlignment(SwingConstants.RIGHT);

        payFrom = new JComboBox<>();

        payFee = new JTextField();
        payFee.setColumns(10);

        JSeparator separator = new JSeparator();

        JLabel lblSem1 = new JLabel("SEM");

        JLabel lblSem2 = new JLabel("SEM");

        JButton paySend = new JButton("Send");
        paySend.setSelected(true);
        paySend.addActionListener(this);
        paySend.setActionCommand(Action.SEND.name());

        JButton payClear = new JButton("Clear");
        payClear.addActionListener(this);
        payClear.setActionCommand(Action.CLEAR.name());

        // @formatter:off
        GroupLayout groupLayout = new GroupLayout(this);
        groupLayout.setHorizontalGroup(
            groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addGap(24)
                    .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(groupLayout.createSequentialGroup()
                            .addGap(38)
                            .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
                                .addComponent(lblTo)
                                .addComponent(lblFrom)
                                .addComponent(lblAmount)
                                .addComponent(lblFee))
                            .addGap(18)
                            .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                                .addComponent(payTo, GroupLayout.DEFAULT_SIZE, 305, Short.MAX_VALUE)
                                .addComponent(payFrom, 0, 305, Short.MAX_VALUE)
                                .addGroup(groupLayout.createSequentialGroup()
                                    .addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
                                        .addComponent(payFee)
                                        .addComponent(payAmount, GroupLayout.DEFAULT_SIZE, 231, Short.MAX_VALUE))
                                    .addPreferredGap(ComponentPlacement.RELATED)
                                    .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
                                        .addComponent(lblSem1)
                                        .addComponent(lblSem2))))
                            .addGap(59))
                        .addGroup(groupLayout.createSequentialGroup()
                            .addComponent(separator, GroupLayout.DEFAULT_SIZE, 453, Short.MAX_VALUE)
                            .addGap(21))))
                .addGroup(groupLayout.createSequentialGroup()
                    .addGap(132)
                    .addComponent(paySend, GroupLayout.PREFERRED_SIZE, 83, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.UNRELATED)
                    .addComponent(payClear, GroupLayout.PREFERRED_SIZE, 84, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(187, Short.MAX_VALUE))
        );
        groupLayout.setVerticalGroup(
            groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addGap(10)
                    .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(lblFrom)
                        .addComponent(payFrom, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGap(18)
                    .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(lblTo)
                        .addComponent(payTo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGap(18)
                    .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(lblAmount)
                        .addComponent(payAmount, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblSem1))
                    .addGap(18)
                    .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(lblFee)
                        .addComponent(payFee, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblSem2))
                    .addPreferredGap(ComponentPlacement.UNRELATED)
                    .addComponent(separator, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.UNRELATED)
                    .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(paySend)
                        .addComponent(payClear))
                    .addContainerGap(139, Short.MAX_VALUE))
        );
        setLayout(groupLayout);
        // @formatter:on

        refresh();
    }

    public String getFrom() {
        return (String) payFrom.getSelectedItem();
    }

    public void setFromItems(List<String> items) {
        payFrom.removeAllItems();
        for (String item : items) {
            payFrom.addItem(item);
        }
    }

    public String getTo() {
        return payTo.getText().trim();
    }

    public void setTo(String to) {
        payTo.setText(to);
    }

    public long getAmount() {
        return (long) (Unit.SEM * Double.parseDouble(payAmount.getText().trim()));
    }

    public void setAmount(double amountSEM) {
        payAmount.setText(String.format("%.3f", amountSEM));
    }

    public long getFee() {
        return (long) (Unit.SEM * Double.parseDouble(payFee.getText().trim()));
    }

    public void setFee(double feeSEM) {
        payFee.setText(String.format("%.3f", feeSEM));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Action action = Action.valueOf(e.getActionCommand());

        switch (action) {
        case REFRESH:
            refresh();
            break;
        case SEND:
            break;
        case CLEAR:
            break;
        default:
            break;
        }
    }

    private void refresh() {
        List<String> addresses = new ArrayList<>();
        Wallet w = Wallet.getInstance();
        for (EdDSA key : w.getAccounts()) {
            addresses.add(key.toString());
        }
        setFromItems(addresses);

        setTo("");

        setAmount(0);

        setFee(Config.MIN_TRANSACTION_FEE / (double) Unit.SEM);
    }
}
