package db61b;

import static db61b.Utils.*;

/** A Column is effectively an index of a specific, named column
 *  in a list of Rows.  Given a sequence of [t0,...,tn] of Tables,
 *  and a column name, c, a Column can retrieve the value of that column of
 *  the first ti that contains it from an array of rows [r0,...,rn],
 *  where each ri comes from ti.
 *  @author P. N. Hilfinger
*/
class Column {
    /** Selects column named NAME from a row of one of the given TABLES. */
    Column(String name, Table... tables) {
        _name = name;
        int cnt = 0, index;
        for (int i = 0; i < tables.length; ++i) {
            index = tables[i].findColumn(name);
            if (index != -1) {
                cnt++;
                _tableName = tables[i].getName();
                _fullName = _tableName + "." + _name;
                // super column | used for getFrom (condition.java)
                _table = i;
                _column = index;
            }
        }
        if (cnt == 0) {
            throw error("Column Error: unknown column: %s", name);
        }
        if (cnt > 1) {
            throw error("Column Error: duplicate columns in tables");
        }
    }

    /** Return complate name */
    String getFullName() {
        return _fullName;
    }

    /** Return _name */
    String getName() {
        return _name;
    }

    /** Return _tableName */
    String getTableName() {
        return _tableName;
    }
    
    /** Return _column */
    int getColumnIndex() {
        return _column;
    }

    /** Return _table */
    // int getTableIndex() {
    //     return _table;
    // }

    /** Returns the value of this Column from ROWS[_table]. Assumes that
     *  ROWS[_table] is from the same table that was provided to the
     *  constructor of this Column. 
     *  
     ** More generally, this method is intended
     ** *such that ROWS[k] coresponds to the kth table that was supplied to
     ** the constructor for this method.
     *
     *  Despite the fact that many rows are passed to this function, this
     *  function returns only one value.
     */
    String getFrom(Row... rows) {
        return rows[_table].get(_column);
    }

    /** Column name denoted by THIS. */
    private String _name, _tableName, _fullName;
    /** Index of the table and column from which to extract a value. */
    private int _table, _column;
}
