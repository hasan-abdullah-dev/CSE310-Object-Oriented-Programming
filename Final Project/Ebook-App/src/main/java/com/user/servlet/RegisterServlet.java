package com.user.servlet;

import java.io.*;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.DAO.UserDAOImpl;
import com.DB.DBConnect;
import com.entity.User;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		try {

			String name = req.getParameter("fname");
			String email = req.getParameter("email");
			String phone = req.getParameter("phone");
			String password = req.getParameter("password");
			String check = req.getParameter("check");

			User us = new User();
			us.setName(name);
			us.setEmail(email);
			us.setPhone(phone);
			us.setPassword(password);
			
			HttpSession session=req.getSession();

			if (check != null) {
				UserDAOImpl dao = new UserDAOImpl(DBConnect.getConn());
				boolean f = dao.userRegister(us);

				if (f) {
					//System.out.println("User Register Successful");
					
					session.setAttribute("succMsg","Registration Successfully");
					resp.sendRedirect("register.jsp");
					
				} else {
					//System.out.println("Something wrong on Server");
					session.setAttribute("failedMsg","Something wrong on Server");
					resp.sendRedirect("register.jsp");
				}

			} else {
				//System.out.println("Please Check Agree & Terms Condition");
				session.setAttribute("failedMsg","Please Check Agree & Terms Condition");
				resp.sendRedirect("register.jsp");
			}
			
			

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
