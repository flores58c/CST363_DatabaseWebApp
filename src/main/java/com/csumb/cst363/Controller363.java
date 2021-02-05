package com.csumb.cst363;

import java.sql.*;
import java.util.ArrayList;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class Controller363 {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	/*
	 * Doctor requests form to create new prescription.
	 */
	@GetMapping("/prescription/new")
	public String newPrescripton(Model model) {
		model.addAttribute("prescription", new Prescription());
		return "prescription_create";
	}
	
	/* 
	 * Process the new prescription form.
	 * 1.  Validate that Doctor SSN exists and matches Doctor Name.
	 * 2.  Validate that Patient SSN exists and matches Patient Name.
	 * 3.  Validate that Drug name exists.
	 * 4.  Insert new prescription.
	 * Return error message and the filled in prescription form
	 *   or the prescription with the rxid number generated by the database.
	 */
	@PostMapping("/prescription")
	public String addPrescription(@Valid Prescription p, BindingResult result, Model model) {
		if (result.hasErrors()) {
			System.out.println("Binding error in addPrescription.");
		}
		System.out.println(p.toString());  // debug -- print form data to console

		KeyHolder holder = new GeneratedKeyHolder();

		try {
			Connection conn = jdbcTemplate.getDataSource().getConnection();

			// no longer needed auto increment rxid
			//trying to count rows and add one for new rxid
		/*
			Statement stmt = conn.createStatement();
			String sql = "select count(rxid) as total from prescription;";
			ResultSet rs = stmt.executeQuery(sql);
			rs.next(); */

			String rx = " ";


			PreparedStatement ps = conn.prepareStatement("insert into prescription ( doctor_ssn," +
					"doctorname,patient_ssn,patientname,drugName,quantity) values(?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);


			//get generated key and set to prescription object


			ps.setString(1, p.getDoctor_ssn());
			ps.setString(2, p.getDoctorName());
			ps.setString(3, p.getPatient_ssn());
			ps.setString(4, p.getPatientName());
			ps.setString(5, p.getDrugName());
			ps.setInt(6, p.getQuantity());
			int count = ps.executeUpdate();

			//this statement gets the generated key from the database :)
			try (ResultSet resultSet = ps.getGeneratedKeys()) {

				if (resultSet.first()) {


					rx = resultSet.getString(1);
				}
			}catch (SQLException ex) {

				return "error/error";
			}



			System.out.println(rx);
			p.setRxid(rx);
			conn.close();
			model.addAttribute("prescription",p);
			model.addAttribute("count", count);
			return "prescription_show";


		} catch (SQLException se) {
			System.out.println("Error:  FirstApp#prescription SQLException " + se.getMessage() );
			model.addAttribute("msg",se.getMessage());
			return "error";
		}
		/*
		 * replace following with code to validate the prescription 
		 * and insert a new prescription
		 */

	}
	
	/* 
	 * patient requests the form to fill a prescription
	 */
	@GetMapping("/prescription/fill")
	public String getfillForm(Model model) {
		model.addAttribute("prescription", new Prescription());
		return "prescription_fill";
	}
	
	/*
	 * process the prescription fill form
	 * 1.  Validate that rxid, pharmacy name and pharmacy address are entered 
	 *     and uniquely identify a prescript and one pharmacy.
	 * 2.  update prescription with pharmacyid, name and address.
	 * 3.  update prescription with today's date.
	 * Display updated prescription 
	 *   or if there is an error show the form with an error message.
	 */
	@PostMapping("/prescription/fill")
	public String processFillForm(@Valid Prescription p, BindingResult result, Model model) {
		if (result.hasErrors()) {
			System.out.println("Binding error in processFillForm.");
		}

		/*
		 * replace the following code with code to validate the rxid, pharmacy name and 
		 * address from the database , and update the pharmacyID, cost and data of 
		 * the prescription
		 */

		try {
			Connection conn = jdbcTemplate.getDataSource().getConnection();

			//represents sql statement
			PreparedStatement ps = conn.prepareStatement("update prescription set pharmacy_id = ?," +
							"pharmacy_name = ?, address = ? , pharmacyphone = ? " +
							"where rxid = ?;");
			ps.setString(2, p.getPharmacyName());
		//	ps.setString(2,p.get  );
			ps.setString(3,  p.getPharmacyAddress());

			ps.setString(5,p.getRxid());

			//
			String vOfPName = p.getPharmacyName();

			Statement st = conn.createStatement();
			String sql = "select pharmacy_id from pharmacy where pharmacy_name = '" + vOfPName + "';";
			ResultSet rs = st.executeQuery(sql);


			String pID= "";
		while(rs.next()){

				pID = rs.getString("pharmacy_id");
				System.out.println("pharmacyID:" + pID);
			}
			p.setPharmacyID(pID);
			ps.setString(1,pID);

			System.out.println("pharmName:" + vOfPName);
			String sql2 = "select phone from pharmacy where pharmacy_name = '" + vOfPName +"';";
			rs = st.executeQuery(sql2);

			String phone = " ";
			while(rs.next()){
				phone = rs.getString("phone");

				System.out.println("pharmacy#:" + phone);
			}

			ps.setString(4,phone);
			p.setPharmacyPhone(phone);
			p.setDateFilled( new java.util.Date().toString() );

			String rxid = p.getRxid();

			String sql3 = "select * from prescription where rxid = " + rxid +";";
			rs=st.executeQuery(sql3);

			while (rs.next())
			{
				// change this to match prescription object
				p.setDoctor_ssn(rs.getString("doctor_ssn"));
				p.setDoctorName(rs.getString("doctorName"));
				p.setPatient_ssn(rs.getString("patient_ssn"));
				p.setPatientName(rs.getString("patientName"));
				p.setDrugName(rs.getString("drugName"));
				p.setQuantity(rs.getInt("quantity"));
			}

			//check if values updated in object
			System.out.print(p.toString());
			//this function sends the values to the database
			int count = ps.executeUpdate();
			model.addAttribute("prescription",p);
			conn.close();
			model.addAttribute("count", count);
			return "prescription_show";
		} catch (SQLException se) {
			System.out.println("Error:  FirstApp#rateform SQLException " + se.getMessage() );
			model.addAttribute("msg",se.getMessage());
			return "error";
		}

	//	p.setPharmacyID("70012345");
	//	p.setCost(12.504);

		
	//	model.addAttribute("prescription", p);
	//	return "prescription_show";

	}
	
	/*
	 * process search request for quantity used for each drug for a given pharmacy.
	 * input is pharmacyID, startDate and endDate
	 * output is drugName, quantity used to fill prescriptions
	 * 1.  validate pharmacyID 
	 */
	@GetMapping("/pharmacy")
	public String pharmacyReport(
			@RequestParam("pharmacyID") String pharmacyID,
			@RequestParam("startDate") String startDate,
			@RequestParam("endDate") String endDate,
			Model model) {
		System.out.println("pharamcy report. ID="+pharmacyID+", start="+startDate+", end="+endDate);  // for debug 
		
		// replace the following code with code to perform database search 
		// returning drugname and quantity used
		ArrayList<ReportElement1> drugs = new ArrayList<>();
		drugs.add(new ReportElement1("Drug1", 5000));
		drugs.add(new ReportElement1("Drug2", 15000));
		drugs.add(new ReportElement1("Drug3", 7500));
		
		
		model.addAttribute("startDate", startDate);
		model.addAttribute("endDate", endDate);
		model.addAttribute("pharmacyID", pharmacyID);
		model.addAttribute("report", drugs);
		return "pharmacy_report";
	}
	
	
	/* 
	 * process search request for FDA of quantity of drug prescribed by doctor
	 * input is drugname, date range
	 * output is doctor name, quantity prescribed
	 * 1.  validate the drug name (it may be a partial name of a drug)
	 */
	@GetMapping("/fda")
	public String fdaReport(
			@RequestParam("drug") String drug, 
			@RequestParam("startDate") String startDate,
			@RequestParam("endDate") String endDate,
			Model model) {
		System.out.println("fda report. drug="+drug+", start="+startDate+", end="+endDate);  // for debug
		
		/*
		 * replace following code with code to perform db search
		 * for drug quantity used by doctors
		 */
		ArrayList<ReportElement1> drugs = new ArrayList<>();
		drugs.add(new ReportElement1("Doctor No", 5000));
		drugs.add(new ReportElement1("Doctor 007", 15000));
		
		
		
		model.addAttribute("startDate", startDate);
		model.addAttribute("endDate", endDate);
		model.addAttribute("drug", drug);
		model.addAttribute("report", drugs);
		return "fda_report";
	}
	
	/*
	 * return JDBC Connection using jdbcTemplate in Spring Server
	 */
	private Connection getConnection() throws SQLException {
		Connection conn = jdbcTemplate.getDataSource().getConnection();
		return conn;
	}
	
}