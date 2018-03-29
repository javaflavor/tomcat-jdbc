package org.openshift.quickstarts.tomcat.dao;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;

import org.apache.commons.dbcp.BasicDataSource;

public class TestDataSource {

    public static void bind() throws NamingException {

        final Context context = createContext();
        try {
            createSubcontext("java:", context);
            createSubcontext("java:comp", context);
            createSubcontext("java:comp/env", context);
            createSubcontext("java:comp/env/jdbc", context);

            final BasicDataSource ds = new BasicDataSource();
            ds.setUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;TRACE_LEVEL_FILE=4");

            context.bind("java:comp/env/"+JdbcTomcatDAO.DB_JNDI, ds);
        } finally {
            context.close();
        }
    }
    
	static Context createContext() throws NamingException {
		System.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.naming.java.javaURLContextFactory");
		System.setProperty(Context.URL_PKG_PREFIXES, "org.apache.naming");
		return new InitialContext();
	}
	
    static void createSubcontext(String name, Context context) throws NamingException {
    	try {
    		context.lookup(name);
    	} catch (NameNotFoundException e) {
    		context.createSubcontext(name);
    	}
    }
    
    public static void unbind() throws NamingException {
        final Context context = createContext();
        try {
            context.unbind("java:comp/env/"+JdbcTomcatDAO.DB_JNDI);
        } finally {
            context.close();
        }    	
    }
    
}
