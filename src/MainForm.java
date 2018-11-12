import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.*;

public class MainForm
{
    //Variable Declaration
    private static final int numberOfSeats = 240;
    private static int numberOfReservedSeats = 0;
    private static double revenue = 0;
    private String name = null;
    private String price = null;
    private String status = "Reserved";
    private String statusCancel = "Canceled";

    //String [] seatArray=new String[240];
    private String [] statusArray=new String[240];
    private String [] nameArray=new String[240];
    private String [] priceArray= new String[240];

    //Min and Max Price Limit
    private final double min=0.0;
    private final double max=1000.0;

    private int row = 0;

    //GUI variables
    private JRadioButton reserveRadio;
    private JRadioButton cancelResvRadio;
    private JTextField nameField;
    private JTextField priceField;
    private JLabel reservedValue;
    private JLabel vacantValue;
    private JLabel revenueValue;
    private JTable reservationsTable;

    //InputUtility Object Creation
    private InputUtility in = new InputUtility();

    //No-Argument Constructor
    MainForm()
    {
    }

    //Constructor with arguments
    MainForm (JRadioButton reserveRadio, JRadioButton cancelResvRadio, JTextField nameField,
                     JTextField priceField, JLabel reservedValue, JLabel vacantValue, JLabel revenueValue, JTable reservationsTable)
    {
        this.reserveRadio = reserveRadio;
        this.cancelResvRadio = cancelResvRadio;
        this.nameField = nameField;
        this.priceField = priceField;
        this.reservedValue = reservedValue;
        this.vacantValue = vacantValue;
        this.revenueValue = revenueValue;
        this.reservationsTable = reservationsTable;
    }

    //method to perform action for Reserve/Cancel Button
    public void buttonOK_Click (JButton button, ActionEvent e)
    {
        boolean inputOk = ReadAndValidateInput(name, price);

        UpdateGUI(name, price);
    }

    //Method to update the GUI
    private void UpdateGUI (String CustomerName, String TicketPrice)
    {
        String seatNumber = String.valueOf(numberOfSeats - numberOfReservedSeats);
        int selectedRow = reservationsTable.getSelectedRow();
        int check = 0;

        if (reserveRadio.isSelected())
        {
            check = canceledSeats();

            if (ReadAndValidateInput(CustomerName, TicketPrice)  && numberOfReservedSeats < numberOfSeats)
            {
                if(check>=0)
                {
                    UpdateArray(check, CustomerName, TicketPrice);

                    reservationsTable.setValueAt(status, check, 1);
                    reservationsTable.setValueAt(name, check, 2);
                    reservationsTable.setValueAt(price, check, 3);
                }

                else
                {
                    UpdateArray(row, CustomerName, TicketPrice);

                    reservationsTable.setValueAt(seatNumber, row, 0);
                    reservationsTable.setValueAt(status, row, 1);
                    reservationsTable.setValueAt(CustomerName, row, 2);
                    reservationsTable.setValueAt(TicketPrice, row, 3);
                    row++;
                }

                numberOfReservedSeats++;
                revenue += in.getPriceInDouble();
            }

            else if (!ReadAndValidateInput(CustomerName, TicketPrice))
            {
                if (!ReadAndValidateName(CustomerName))
                    JOptionPane.showMessageDialog(
                            null, "Invalid Name input!\nPlease try again", "Invalid Name",
                            JOptionPane.WARNING_MESSAGE);

                else if (!ReadAndValidatePrice(price))
                    JOptionPane.showMessageDialog(
                            null, "Invalid Price input!\nPlease try again", "Invalid Price",
                            JOptionPane.WARNING_MESSAGE);
            }
        }

        else if (cancelResvRadio.isSelected())
        {
            if (!nameField.getText().isEmpty())
            {
                for (int i = 0; i < 240; i++)
                {
                    if (nameField.getText().equals(reservationsTable.getValueAt(i, 2)))
                    {
                        if (!statusCancel.equals(reservationsTable.getValueAt(i, 1)))
                        {
                            reservationsTable.setValueAt(statusCancel, i, 1);
                            statusArray[i] = statusCancel;
                            numberOfReservedSeats--;
                            revenue -= Double.parseDouble(String.valueOf(reservationsTable.getValueAt(i, 3)));
                            break;
                        }
                    }
                }
            }

            else if (nameField.getText().isEmpty() && reservationsTable.isRowSelected(selectedRow))
            {
                if (!statusCancel.equals(reservationsTable.getValueAt(selectedRow, 1)))
                {
                    reservationsTable.setValueAt(statusCancel, selectedRow, 1);
                    statusArray[selectedRow] = statusCancel;
                    numberOfReservedSeats--;
                    revenue -= Double.parseDouble(String.valueOf(reservationsTable.getValueAt(selectedRow, 3)));
                }
            }
        }

        UpdateOutputPanel(numberOfReservedSeats, revenue);

        nameField.setText("");
        priceField.setText("");

        writeData1File();
        writeFileAtClose();
    }

    //Method to check canceled reserved seats
    private int canceledSeats()
    {
        int x=-1;
        for(int i=0;i<row;i++)
        {
            if(statusArray[i].equals(statusCancel))
            {
                x=i;
                break;
            }
        }
        return x;
    }

    //Update the output panel
    void UpdateOutputPanel (int totalReserved, double totalRevenue)
    {
        reservedValue.setText(String.valueOf(totalReserved));
        vacantValue.setText(String.valueOf(numberOfSeats - totalReserved));
        revenueValue.setText(Double.toString(totalRevenue));
    }

    //Arrays to store and write table data into file
    private void UpdateArray (int r, String Customer, String Ticket)
    {
        statusArray[r] = status;
        nameArray[r] = Customer;
        priceArray[r] = Ticket;
    }

    //Read and Validate both name and price
    private boolean ReadAndValidateInput(String name, String price)
    {
        return ReadAndValidateName(name) && ReadAndValidatePrice(price);
    }

    //Read and validate name to check for any invalid characters
    private boolean ReadAndValidateName(String name)
    {

        if(name.isEmpty() || name.matches(".*[!-,/-@^-`|-~]+.*"))
        {
            //System.out.println("Invalid input!");
            return false;
        }
        else
            return true;
    }

    //Read and validate price to check invalid number
    private boolean ReadAndValidatePrice(String price)
    {
        if (price.isEmpty() || price.matches(".*[ --:-~/]+.*"))
        {
            //System.out.println("Invalid price");
            return false;
        }
        else
        {
            if (in.getDouble(price, min, max))
                return true;
            else
            {
                //System.out.println("Invalid price");
                return false;
            }
        }
    }

    //Name setter
    public void setName(String name)
    {
        this.name = name;
    }

    //Price setter
    public void setPrice(String price)
    {
        this.price = price;
    }


    //Read files at program start
    public void updateFile()
    {
        File f1 = new File("src//data1.bin");

        if(f1.exists())
            readData1File();

        readFileAtStart();
    }

    //Write output panel's information into file
    private void writeData1File()
    {
        try (PrintWriter pw = new PrintWriter("src//data1.bin"))
        {
            pw.println(Double.toString(revenue));
            pw.println((Integer.toString(numberOfReservedSeats)));
            pw.println((Integer.toString(row)));
        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog(null,"Ops! File could not be Created!Please Relaunch the Program","File Creation Error",JOptionPane.WARNING_MESSAGE);
        }
    }

    //Read output panel's information into file
    private void readData1File()
    {
        try (Scanner sc = new Scanner(new File("src//data1.bin")))
        {
            while (sc.hasNext())
            {
                revenue = Double.parseDouble(sc.nextLine());
                numberOfReservedSeats = Integer.parseInt(sc.nextLine());
                row = Integer.parseInt(sc.nextLine());

                UpdateOutputPanel(numberOfReservedSeats, revenue);
            }
        }
        catch(Exception e)
        {
            JOptionPane.showMessageDialog(null,"Ops! File could not be read!\nPlease Relaunch the Program","Data Read Error",JOptionPane.WARNING_MESSAGE);
        }
    }

    //Saves table data into file when reserve/cancel button is clicked
    private void writeFileAtClose()
    {
        try (PrintWriter pw = new PrintWriter("src//data2.bin"))
        {
            for(int i=0;i<row;i++)
            {
                pw.println(String.format("%s",statusArray[i]));
                pw.println(String.format("%s",nameArray[i]));
                pw.println(String.format("%s",priceArray[i]));
            }
        }
        catch(Exception e)
        {
            JOptionPane.showMessageDialog(null,"Ops! Data could not be Written!\nPlease Relaunch the Program","Data Creation Error",JOptionPane.WARNING_MESSAGE);
        }
    }

    //Read table data from file when the program starts
    private void readFileAtStart()
    {
        File f1 = new File("src//data2.bin");

        if(f1.exists())
        {
            try (Scanner sc = new Scanner(new File("src//data2.bin")))
            {
                int x;

                for (int i = 0; i < row; i++)
                {
                    statusArray[i] = sc.nextLine();
                    nameArray[i] = sc.nextLine();
                    priceArray[i] = sc.nextLine();

                    x = 240 - i;

                    reservationsTable.setValueAt(Integer.toString(x), i, 0);
                    reservationsTable.setValueAt(statusArray[i], i, 1);
                    reservationsTable.setValueAt(nameArray[i], i, 2);
                    reservationsTable.setValueAt(priceArray[i], i, 3);

                }
                sc.close();

            }
            catch (Exception e)
            {
                JOptionPane.showMessageDialog(null, "Data could not be read!\nPlease Relaunch The Program", "Data read error", JOptionPane.WARNING_MESSAGE);
            }
        }
    }
}
