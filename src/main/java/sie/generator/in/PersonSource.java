package sie.generator.in;

import sie.generator.Generator;

/**
 *
 * @author Håkan Lidén
 */
public class PersonSource {

    private String name;
    private String image;
    private String pin;
    private String email;
    private String address;
    private String phone;
    private String password;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSignature() {
        String[] parts = name.split(" ");
        return parts[0].substring(0, 1) + parts[1].substring(0, 1);
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPin() {
        return pin;
    }

    public String getFormatedPin() {
        return pin.substring(2, 8) + "-" + pin.substring(8);
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public String getFormatedPostalAddress() {
        String postalAddress = address.substring(address.indexOf(",\n") + 2);
        String city = Generator.capitalize(postalAddress.substring(0, postalAddress.indexOf(",")).trim());
        return postalAddress.substring(postalAddress.indexOf(",") + 2).trim() + " " + city;
    }

    public String getFormatedStreetAddress() {
        return address.substring(0, address.indexOf(",\n")).trim();
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
