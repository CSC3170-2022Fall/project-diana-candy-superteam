// This is a SUGGESTED skeleton for a class that represents a single
// Table.  You can throw this away if you want, but it is a good
// idea to try to understand it first.  Our solution changes or adds
// about 100 lines in this skeleton.

// Comments that start with "//" are intended to be removed from your
// solutions.
package db61b;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import static db61b.Utils.*;

/** A single table in a database.
 *  @author P. N. Hilfinger
 */
class Table implements Iterable<Row> {
    /** A new Table whose columns are given by COLUMNTITLES, which may
     *  not contain dupliace names. */
    Table(String[] columnTitles, String... tableName) {
        /** check the validity of column names */
        for (int i = columnTitles.length - 1; i >= 1; i -= 1) {
            for (int j = i - 1; j >= 0; j -= 1) {
                if (columnTitles[i].equals(columnTitles[j])) {
                    throw error("Table Error: duplicate column name: %s",
                                columnTitles[i]);
                }
            }
        }

        if (tableName.length > 0) _tableName = tableName[0];
        _columnTitles = columnTitles;
        _rows = new HashSet<Row>();
        _columns = new ArrayList<Column>();
        for (String title : _columnTitles) {
            _columns.add(new Column(title, this));
        }
    } 

    /** A new Table constructed by given columns */
    Table(List<Column> columns, String... tableName) {
        for (int i = columns.size() - 1; i >= 1; i -= 1) {
            for (int j = i - 1; j >= 0; j -= 1) {
                if (columns.get(i).equals(columns.get(j))) {
                    throw error("Table Error: duplicate column: %s",
                                columns.get(i).getFullName());
                }
            }
        }
        
        _columnTitles = new String[columns.size()];
        for (int i = 0; i < columns.size(); ++i) {
            int cnt = 0;
            for (Column c : columns) {
                if (columns.get(i).getName().equals(c.getName())) cnt++;
            }

            if (cnt == 1) {
                _columnTitles[i] = columns.get(i).getName();
            }
            else { // count(getName) > 1 means there are duplicate names, use full name instead.
                _columnTitles[i] = columns.get(i).getFullName();
            }
        }
        if (tableName.length > 0) _tableName = tableName[0];
        _rows = new HashSet<Row>();
        _columns = columns;
    }

    /** Return the table name */
    public String getName() {
        return _tableName;
    }

    /** Return the column list */
    public List<Column> columns() {
        return _columns;
    }

    /** Return the number of columns in this table. */
    public int columnSize() {
        return _columnTitles.length;
    }

    /** Return the complate title of the Kth column */
    public String getFullTitle(int k) {
        if (k >= 0 && k < _columnTitles.length) return _tableName+"."+_columnTitles[k];
        return "";
    }

    /** Return the title of the Kth column.  Requires 0 <= K < columns(). */
    public String getTitle(int k) {
        if (k >= 0 && k < _columnTitles.length) return _columnTitles[k];
        return "";
    }

    /** Return the number of the column whose title is TITLE, or -1 if
     *  there isn't one. */
    public int findColumn(String title) {
        for (int i = 0; i < _columnTitles.length; ++i) {
            if (getTitle(i).equals(title) || getFullTitle(i).equals(title)) {
                return i;
            }
        }
        return -1;
    }

    /** Return the number of Rows in this table. */
    public int size() {
        return _rows.size();
    }

    /** Returns an iterator that returns my rows in an unspecfied order. */
    @Override
    public Iterator<Row> iterator() {
        return _rows.iterator();
    }

    /** Add ROW to THIS if no equal row already exists.  Return true if anything
     *  was added, false otherwise. */
    public boolean add(Row row) {
        return _rows.add(row);
    }

    // !! ------------------------ Important functions ------------------------

    /** Read the contents of the file NAME.db, and return as a Table.
     *  Format errors in the .db file cause a DBException. */
    static Table readTable(String name) {
        BufferedReader input = null;
        Table table = null;
        try { 
            input = new BufferedReader(new FileReader("./testing/"+name + ".db"));
            String header = input.readLine();
            if (header == null) {
                throw error("Table Error: header missing in .db file");
            }
            String[] columnNames = header.split(",");
            table = new Table(columnNames, name);
            String line;
            while ((line = input.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length != columnNames.length) {
                    throw error("Table Error: attributes number inconsistency in .db file");
                }
                Row row = new Row(values);
                if (!table.add(row)) {
                    throw error("Table Error: duplicate row in .db file");
                }
            }
        } catch (FileNotFoundException e) {
            throw error("could not find %s.db", name);
        } catch (IOException e) {
            throw error("unexpected problem reading from %s.db", name);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    /* Ignore IOException */
                }
            }
        }
        return table;
    }

    /** Write the contents of TABLE into the file NAME.db. Any I/O errors
     *  cause a DBException. */
    // ! nickname 暂时不支持（没必要，store 指令调用的一般是 insert 后的 table）
    void writeTable(String name) {
        PrintStream output = null;
        try {
            String sep;
            sep = "";
            output = new PrintStream("./testing/"+name + ".db");
            for (String title : _columnTitles) {
                output.print(sep);
                output.print(title);
                sep = ",";
            }
            output.println();

            for (Row row : _rows) {
                sep = "";
                for (String value : row.getAll()) {
                    output.print(sep);
                    output.print(value);
                    sep = ",";
                }
                output.println();
            }
        } catch (IOException e) {
            throw error("unexpected problem writing to %s.db", name);
        } finally {
            if (output != null) {
                output.close();
            }
        }
    }

    /** translate table to String (used for print) */
    public String toString() {
        String result = "";
        // String sep;
        for (String title : _columnTitles) {
            result += String.format("%1$-"+outputFormat+"s", title);
        }
        String sepLine = "-".repeat(result.length());
        result += "\n";
        
        result += sepLine+"\n";

        for (Row row : _rows) {
            for (String value : row.getAll()) {
                result += String.format("%1$-"+outputFormat+"s", value);
            }
            result += "\n";
        }
        return result;
    }

    /** Print my contents on the standard output. */
    void print() {
        System.out.print(this);
    }

    /** Return the cartesian product of two tables. */
    Table join(Table table2, List<Condition> conditions) {
        List<Column> columns = new ArrayList<Column>();
        for (Column column : _columns)        columns.add(column);
        for (Column column : table2._columns) columns.add(column);

        Table result = new Table(columns);
        for (Row row1 : this) {
            for (Row row2 : table2) {
                Row row = row1.join(row2);
                if (Condition.test(conditions, row)) {
                    result.add(row);
                }
            }
        }
        return result;
    }

    /** Return the cartesian product of multiple tables. */
    Table join(List<Table> tables, List<Condition> conditions) {
        if (tables.size() == 0) {
            return this;
        }
        Table result = this;
        for (Table table : tables) {
            result = result.join(table, conditions);
        }
        return result;
    }

    /** Return a new Table whose columns are COLUMNNAMES, selected from
     *  rows of this table that satisfy CONDITIONS. */
    Table select(List<String> columnNames, List<Condition> conditions) {
        if (columnNames == null) { // select *
            Table result = new Table(_columns);
            for (Row row : this) {
                if (Condition.test(conditions, row)) {
                    result.add(row);
                }
            }
            return result;
        }
        else { // select ,
            List<Column> columns = new ArrayList<Column>(); 
            for (String columnName : columnNames) {
                for (int i = 0; i < _columnTitles.length; ++i) {
                    if (_columnTitles[i].equals(columnName)) {
                        columns.add(_columns.get(i));
                        break;
                    }
                }
            }
            Table result = new Table(columns);
            for (Row row : this) { // iterate over rows of this table
                if (Condition.test(conditions, row)) {
                    List<String> values = new ArrayList<String>();
                    for (String columnName : columnNames) {
                        int ok = 0;
                        for (int i = 0; i < _columnTitles.length; ++i) {
                            if (_columnTitles[i].equals(columnName)) {
                                values.add(row.get(i));
                                ok = 1;
                                break;
                            }
                        }
                        if (ok == 0) {
                            throw error("Table.select Error: column name not found");
                        }
                    }
                    result.add(new Row(values));
                }
            }
            return result;
        }
    }

    /** Return a new Table whose columns are COLUMNNAMES, selected
     *  from pairs of rows from this table and from TABLE2 that match
     *  on all columns with identical names and satisfy CONDITIONS. */
    Table select(Table table2, List<String> columnNames,
                 List<Condition> conditions) {
        List<Column> columns = new ArrayList<Column>(); 
        for (String columnName : columnNames) {
            for (int i = 0; i < _columnTitles.length; ++i) {
                if (_columnTitles[i].equals(columnName)) {
                    columns.add(_columns.get(i));
                    break;
                }
            }
        }
        Table result = new Table(columns);
        List<Column> common1 = new ArrayList<>();
        List<Column> common2 = new ArrayList<>();
        
        for (Column c1 : this.columns()) {
            for (Column c2 : table2.columns()) {
                if (c1.getName().equals(c2.getName())) {
                    common1.add(c1);
                    common2.add(c2);
                }
            }
        }

        for (Row row1 : this) {
            for (Row row2 : table2) {
                if (equijoin(common1, common2, row1, row2) && Condition.test(conditions, row1, row2)) {
                    ArrayList<String> values = new ArrayList<String>();
                    for (int i = 0; i < _columnTitles.length; ++i) {
                        if (columnNames.contains(_columnTitles[i])) {
                            values.add(row1.get(i));
                        }
                    }
                    result.add(new Row(values));
                }
            }
        }
        return result;
    }

    /** Return true if the columns COMMON1 from ROW1 and COMMON2 from
     *  ROW2 all have identical values.  Assumes that COMMON1 and
     *  COMMON2 have the same number of elements and the same names,
     *  that the columns in COMMON1 apply to this table, those in
     *  COMMON2 to another, and that ROW1 and ROW2 come, respectively,
     *  from those tables. */
    private static boolean equijoin(List<Column> common1, List<Column> common2,
                                    Row row1, Row row2) {
        if (common1.size() != common2.size()) return false;
        if (row1.size() != row2.size()) return false;

        for (int i = 0; i < common1.size(); i++) {
            if (!row1.get(common1.get(i).getColumnIndex()).equals(row2.get(common2.get(i).getColumnIndex()))) {
                return false;
            }
        }
        return true;
    }

    /** My rows. */
    private int outputFormat = 12;
    private String _tableName = null;
    private HashSet<Row> _rows;
    private String[] _columnTitles;
    private List<Column> _columns;
}
