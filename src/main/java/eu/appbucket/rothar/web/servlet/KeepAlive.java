package eu.appbucket.rothar.web.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class KeepAlive extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private String appVersion;
	
	public KeepAlive() {
		super();
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		appVersion = config.getInitParameter("version");
	}
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		String outputTemplate = "<h1>Rothar is alive, time: %s, version: %s</h1>";
		String output = String.format(outputTemplate, formatter.format(currentTime), appVersion);
		out.println(output);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		this.doGet(request, response);
	}
}
