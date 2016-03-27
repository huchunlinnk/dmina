package cn.hucl.dmina.data;

import java.io.Serializable;


public class UserInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String user_name;
	private String password;
	private String sex;
	private String age;
	private String department;
	private String grade;
	private String student_id;
	private String phone_num;
	private String email;
	private String bluetooth_address;
	private int wifi_ip_address;
	private String wifi_mac_address;
	private double latitude;
	private double longitude;
	private String location;
	public String getUser_name() {
		return user_name;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getAge() {
		return age;
	}
	public void setAge(String age) {
		this.age = age;
	}
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
	}
	public String getGrade() {
		return grade;
	}
	public void setGrade(String grade) {
		this.grade = grade;
	}
	public String getStudent_id() {
		return student_id;
	}
	public void setStudent_id(String student_id) {
		this.student_id = student_id;
	}
	public String getPhone_num() {
		return phone_num;
	}
	public void setPhone_num(String phone_num) {
		this.phone_num = phone_num;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getBluetooth_address() {
		return bluetooth_address;
	}
	public void setBluetooth_address(String bluetooth_address) {
		this.bluetooth_address = bluetooth_address;
	}
	public int getWifi_ip_address() {
		return wifi_ip_address;
	}
	public void setWifi_ip_address(int wifi_ip_address) {
		this.wifi_ip_address = wifi_ip_address;
	}
	public String getWifi_mac_address() {
		return wifi_mac_address;
	}
	public void setWifi_mac_address(String wifi_mac_address) {
		this.wifi_mac_address = wifi_mac_address;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
}
