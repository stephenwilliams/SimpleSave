package com.alta189.simplesave;

import com.alta189.simplesave.exceptions.ConnectionException;
import com.alta189.simplesave.exceptions.UnknownTableException;
import com.alta189.simplesave.internal.TableFactory;
import com.alta189.simplesave.internal.TableRegistration;
import com.alta189.simplesave.exceptions.TableRegistrationException;
import com.alta189.simplesave.query.Query;
import com.alta189.simplesave.query.QueryResult;
import com.alta189.simplesave.query.SelectQuery;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public abstract class Database {

	private final Map<Class<?>, TableRegistration> tables = new HashMap<Class<?>, TableRegistration>();
	private Logger logger = Logger.getLogger(getClass().getCanonicalName());
	private boolean lock = false;

	protected Database() {

	}

	public Logger getLogger() {
		return logger;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	public void registerTable(Class<?> tableClass) throws TableRegistrationException {
        if (lock)
	        throw new TableRegistrationException("The database is connected. You cannot register a new table");
		TableRegistration table = TableFactory.buildTable(tableClass);
		if (table == null)
			throw new TableRegistrationException("The TableFactory returned a null table");
		tables.put(tableClass, table);
   	}

	protected Map<Class<?>, TableRegistration> getTables() {
		return tables;
	}

	public TableRegistration getTableRegistration(Class<?> tableClass) {
		return tables.get(tableClass);
	}

	public void connect() throws ConnectionException {
		lock = true;
	}

	public void close() throws ConnectionException {
		lock = false;
	}

	public <T> SelectQuery<T> select(Class<T> tableClass) {
		if (getTableRegistration(tableClass) == null)
			throw new UnknownTableException("Cannot select from an unregistered table!");
		return new SelectQuery<T>(this, tableClass);
	}

	public abstract boolean isConnected();

	public abstract <T> QueryResult<T> execute(Query<T> query);

	public abstract void save(Class<?> tableClass, Object o);

}
