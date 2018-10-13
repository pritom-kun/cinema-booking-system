public class InputUtility
{
    //Data fields
    private double priceInDouble;

    //No-argument Constructor
    InputUtility()
    {
    }

    //Converts string into double and checks the range
    public boolean getDouble(String price, double minlimit, double maxlimit)
    {
        priceInDouble = Double.parseDouble(price);

        return priceInDouble >= minlimit && priceInDouble <= maxlimit;
    }

    //getter method for priceInDouble
    public double getPriceInDouble()
    {
        return priceInDouble;
    }
}
