package de.gedoplan.sample.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map.Entry;
import java.util.Properties;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Helferklasse für JDBC-Anwendungen.
 * 
 * @author dw
 */
public final class JdbcUtil
{
  private static Properties dbProperties = null;

  private static final Log  LOGGER       = LogFactory.getLog(JdbcUtil.class);

  /**
   * DB-Properties liefern.
   * 
   * Beim ersten Aufruf wird die Classpath-Ressource "dbproperties.xml" als Properties-Objekt eingelesen.
   * 
   * Die Properties enthalten zumindest diese Einträge:
   * <ul>
   * <li>javax.persistence.jdbc.driver: Treiber-Klasse</li>
   * <li>javax.persistence.jdbc.url: Connect-URL</li>
   * <li>javax.persistence.jdbc.user: User</li>
   * <li>javax.persistence.jdbc.password: Passwort
   * <li>
   * </ul>
   * 
   * @return DB-Properties
   */
  public static Properties getDbProperties()
  {
    if (dbProperties == null)
    {
      dbProperties = loadProperties("dbproperties.xml");

      if (dbProperties.containsKey("javax.persistence.jdbc.driver"))
      {
        if (LOGGER.isDebugEnabled())
        {
          LOGGER.debug("dbProperties:");
          for (Entry<Object, Object> entry : dbProperties.entrySet())
          {
            LOGGER.debug("  " + entry.getKey() + " = " + entry.getValue());
          }
        }
      }
      else
      {
        LOGGER.warn("JDBC-Treiber nicht gesetzt; vermutlich fehlt dbProperties.xml im Classpath!");
      }
    }

    return dbProperties;
  }

  /**
   * Verbindung zur DB aufbauen.
   * 
   * @return Connection
   * @throws ClassNotFoundException falls Treiberklasse nicht gefunden
   * @throws SQLException bei allgemeinen Fehlern
   */
  public static Connection getConnection() throws ClassNotFoundException, SQLException
  {
    getDbProperties();

    String driverClassName = dbProperties.getProperty("javax.persistence.jdbc.driver");
    String connectUrl = dbProperties.getProperty("javax.persistence.jdbc.url");
    String user = dbProperties.getProperty("javax.persistence.jdbc.user");
    String pwd = dbProperties.getProperty("javax.persistence.jdbc.password");

    Class.forName(driverClassName);

    Properties prop = new Properties();
    if (user != null)
    {
      prop.setProperty("user", user);
    }
    if (pwd != null)
    {
      prop.setProperty("password", pwd);
    }

    return DriverManager.getConnection(connectUrl, prop);
  }

  private static Properties loadProperties(String resourceName)
  {
    Properties prop = new Properties();

    InputStream inputStream = null;
    XMLEventReader xmlEventReader = null;
    try
    {
      inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName);
      xmlEventReader = XMLInputFactory.newInstance().createXMLEventReader(inputStream);
      while (xmlEventReader.hasNext())
      {
        XMLEvent xmlEvent = xmlEventReader.nextEvent();
        if (xmlEvent.isStartElement())
        {
          StartElement startElement = xmlEvent.asStartElement();
          if ("property".equals(startElement.getName().getLocalPart()))
          {
            Attribute nameAttribute = startElement.getAttributeByName(new QName("name"));
            Attribute valueAttribute = startElement.getAttributeByName(new QName("value"));
            if (nameAttribute != null && valueAttribute != null)
            {
              prop.setProperty(nameAttribute.getValue(), valueAttribute.getValue());
            }
          }
        }
      }
    }
    catch (Throwable e)
    {
      // ignore
    }
    finally
    {
      try
      {
        xmlEventReader.close();
      }
      catch (Throwable e)
      {
        // ignore
      }

      try
      {
        inputStream.close();
      }
      catch (Throwable e)
      {
        // ignore
      }
    }

    return prop;
  }

  /*
   * Keine Instantiierung erlauben
   */
  private JdbcUtil()
  {
  }
}
