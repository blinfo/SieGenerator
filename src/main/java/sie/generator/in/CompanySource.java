package sie.generator.in;

import sie.generator.Generator;

/**
 *
 * @author Håkan Lidén
 */
public class CompanySource {

    private String companyName;
    private String orgNum;
    private String vatCode;
    private String address;
    private String bankgiro;
    private String contact;
    private String contactEmail;
    private String contactPhone;
    private String type;

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getOrgNum() {
        return orgNum;
    }

    public void setOrgNum(String orgNum) {
        this.orgNum = orgNum;
    }

    public String getVatCode() {
        return vatCode;
    }

    public void setVatCode(String vatCode) {
        this.vatCode = vatCode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String adress) {
        this.address = adress;
    }

    public String getFormatedPostalAddress() {
        String postalAddress = address.substring(address.indexOf(",\n") + 2);
        String city = Generator.capitalize(postalAddress.substring(0, postalAddress.indexOf(",")).trim());
        return postalAddress.substring(postalAddress.indexOf(",") + 2).trim() + " " + city;
    }

    public String getFormatedStreetAddress() {
        return address.substring(0, address.indexOf(",\n")).trim();
    }

    public String getBankgiro() {
        return bankgiro;
    }

    public void setBankgiro(String bankgiro) {
        this.bankgiro = bankgiro;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
