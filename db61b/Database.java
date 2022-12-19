// This is a SUGGESTED skeleton for a class that contains the Tables your
// program manipulates.  You can throw this away if you want, but it is a good
// idea to try to understand it first.  Our solution changes about 6
// lines in this skeleton.

// Comments that start with "//" are intended to be removed from your
// solutions.
package db61b;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static db61b.Utils.*;

/** A collection of Tables, indexed by name.
 *  @author */
class Database {
    /** An empty database. */
    public Database() {
        tables_ = new ArrayList<Table>();
        tableNames_ = new ArrayList<String>();
    }

    /** Return the Table whose name is NAME stored in this database, or null
     *  if there is no such table. */
    public Table get(String name) {
        if (tableNames_.contains(name)) {
            return tables_.get(tableNames_.indexOf(name));
        }
        return null;
    }

    /** Set or replace the table named NAME in THIS to TABLE.  TABLE and
     *  NAME must not be null, and NAME must be a valid name for a table. */
    public void put(String name, Table table) {
        if (name == null || table == null) {
            throw new IllegalArgumentException("Database.put Error: null argument");
        } 
        // replace table
        else if (tableNames_.contains(name)) {
            tables_.set(tableNames_.indexOf(name), table);
        } 
        // add table
        else {
            tables_.add(table);
            tableNames_.add(name);
        }
    }

    public void remove(String name) {
        if (name == null) {
            throw error("no such table: %s", name);
        } 
        else {
            int index = tableNames_.indexOf(name);
            if (index == -1) {
                throw error("no such table: %s", name);
            }
            else {
                tables_.remove(index);
                tableNames_.remove(name);
            }
        }
    }

    /** Return an iterator over all the tables in the database. */
    public Iterator<Table> iterator() {
        return tables_.iterator();
    }

    /** Return a list of the names of all the tables in the database. */
    public List<String> tableNames() {
        return tableNames_;
    }

    /** My tables. */
    private ArrayList<Table> tables_;
    /** My table names. */
    private ArrayList<String> tableNames_;
}
