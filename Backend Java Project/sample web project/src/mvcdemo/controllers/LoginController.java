package mvcdemo.controllers;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import pack.Jdbc;

import sun.text.normalizer.ICUBinary.Authenticate;
import com.mysql.jdbc.log.Log;


public class LoginController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Jdbc jdbc = new Jdbc();

	public LoginController() {
		super();
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		String username = request.getParameter("username");
		String password = request.getParameter("password");
		RequestDispatcher rd = null;
		
		
		String result =jdbc.ConfirmEmail(username,password);

		if (result.equals("Success")) {
			rd = request.getRequestDispatcher("/success.jsp");
			
		} else {
			rd = request.getRequestDispatcher("/error.jsp");
		}
		rd.forward(request, response);
	}

}