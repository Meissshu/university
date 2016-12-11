package name.allayarovmarsel.app.entities;

/**
 * This class describes an element of university table - the university.
 */
public class UniversityObject {

    /**
     * Short name of the university
     */
    private String shortName;
    /**
     * Long name of the university
     */
    private String longName;

    /**
     * Defines the university visibility in the table
     * True = visible, false = invisible
     */
    private boolean ifShown;

    /**
     * Creates an university with short name sN, long name lN and visibility parameter
     * @param sN  the short name of the university
     * @param lN  the long name of the university
     * @param ifS  the visibility of the university
     */
    public UniversityObject(String sN, String lN, boolean ifS){
        shortName = sN;
        longName = lN;
        ifShown = ifS;
    }

    /**
     * Creates an university with only short name
     * @param sN  the short name of the university
     */
    public UniversityObject(String sN)
    {
        shortName = sN;
    }

    /**
     * Sets the parameter {@link UniversityObject#ifShown}
     * @param ifShown  True = shows up, false = doesn't show up
     */
    public void setIfShown(boolean ifShown) {
        this.ifShown = ifShown;
    }

    /**
     * Returns the short name of the university
     * @return {@link UniversityObject#shortName}
     */
    public String getShortName() {
        return shortName;
    }

    /**
     * Return the long name of the university
     * @return {@link UniversityObject#longName}
     */

    public String getLongName() {
        return longName;
    }

    /**
     * Returns the <code>boolean</code> ifShown of the university
     * @return {@link UniversityObject#ifShown}
     */
    public boolean getIfShown() {
        return ifShown;
    }


    /**
     * Tests for equality
     * @param obj  the object to test
     * @return  true if short names are equal
     */
    @Override
    public boolean equals(Object obj) {

        if (obj == null) return false;
        if (obj == this) return true;
        if (!(obj instanceof UniversityObject)) return false;
        UniversityObject o = (UniversityObject) obj;
        return (o.shortName.equals(this.shortName));
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public String toString(){
        return (getShortName() + " " + getLongName() + " " + getIfShown());
    }
}
