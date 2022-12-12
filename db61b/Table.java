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
    Table(String[] columnTitles) {
        /** check the validity of column names */
        for (int i = columnTitles.length - 1; i >= 1; i -= 1) {
            for (int j = i - 1; j >= 0; j -= 1) {
                if (columnTitles[i].equals(columnTitles[j])) {
                    throw error("duplicate column name: %s",
                                columnTitles[i]);
                }
            }
        }
        _columnTitles = columnTitles;
        _rows = new HashSet<Row>();
        _columns = new ArrayList<Column>();
        for (String title : columnTitles) {
            _columns.add(new Column(title, this));
        }
    } 

    /** A new Table whose columns are give by COLUMNTITLES. */
    Table(List<String> columnTitles) {
        this(columnTitles.toArray(new String[columnTitles.size()]));
    }

    /** Return the column list */
    public List<Column> columns() {
        return _columns;
    }

    /** Return the number of columns in this table. */
    public int columnSize() {
        return _columnTitles.length;
    }

    /** Return the title of the Kth column.  Requires 0 <= K < columns(). */
    public String getTitle(int k) {
        return _columnTitles[k];
    }

    /** Return the number of the column whose title is TITLE, or -1 if
     *  there isn't one. */
    public int findColumn(String title) {
        for (int i = 0; i < _columnTitles.length; ++i) {
            if (_columnTitles[i].equals(title)) {
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

    /** Read the contents of the file NAME.db, and return as a Table.
     *  Format errors in the .db file cause a DBException. */
    static Table readTable(String name) {
        BufferedReader input = null;
        Table table = null;
        try { 
            input = new BufferedReader(new FileReader("D:/DataBaseProject/project-diana-candy-superteam/testing/"+name + ".db"));
            String header = input.readLine();
            if (header == null) {
                throw error("missing header in DB file");
            }
            String[] columnNames = header.split(",");
            table = new Table(columnNames);
            String line;
            while ((line = input.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length != columnNames.length) {
                    throw error("wrong number of values in DB file");
                }
                Row row = new Row(values);
                if (!table.add(row)) {
                    throw error("duplicate row in DB file");
                }
            }
        } catch (FileNotFoundException e) {
            throw error("could not find %s.db", name);
        } catch (IOException e) {
            throw error("problem reading from %s.db", name);
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
            throw error("trouble writing to %s.db", name);
        } finally {
            if (output != null) {
                output.close();
            }
        }
    }

    /** Print my contents on the standard output. */
    void print() {
        String sep;
        sep = "";
        for (String title : _columnTitles) {
            System.out.print(sep);
            System.out.print(title);
            sep = "\t";
        }
        System.out.println();

        for (Row row : _rows) {
            sep = "";
            for (String value : row.getAll()) {
                System.out.print(sep);
                System.out.print(value);
                sep = "\t";
            }
            System.out.println();
        }
    }

    /** Return the cartesian product of two tables. */
    Table join(Table table2, List<Condition> conditions) {
        List<String> columnNames = new ArrayList<>();
        for (String title : _columnTitles)         columnNames.add(title);
        for (String title : table2._columnTitles)  columnNames.add(title);

        Table result = new Table(columnNames);
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
        Table result = new Table(columnNames);
        for (Row row : this) { // iterate over rows of this table
            if (Condition.test(conditions, row)) {
                result.add(row.select(columnNames));
            }
        }
        return result;
    }

    /** Return a new Table whose columns are COLUMNNAMES, selected
     *  from pairs of rows from this table and from TABLE2 that match
     *  on all columns with identical names and satisfy CONDITIONS. */
    Table select(Table table2, List<String> columnNames,
                 List<Condition> conditions) {
        Table result = new Table(columnNames);
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
                    result.add(row1.select(columnNames));
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
    private HashSet<Row> _rows;
    private String[] _columnTitles;
    private List<Column> _columns;
}

