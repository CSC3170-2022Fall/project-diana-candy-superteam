// This is a SUGGESTED skeleton for a class that describes a single
// Condition (such as CCN = '99776').  You can throw this away if you
// want,  but it is a good idea to try to understand it first.
// Our solution changes or adds about 30 lines in this skeleton.

// Comments that start with "//" are intended to be removed from your
// solutions.
package db61b;

import java.util.List;
import static db61b.Utils.*;

/** Represents a single 'where' condition in a 'select' command.
 *  @author */
class Condition {

    /** A Condition representing COL1 RELATION COL2, where COL1 and COL2
     *  are column designators. and RELATION is one of the
     *  strings "<", ">", "<=", ">=", "=", or "!=". */
    Condition(Column col1, String relation, Column col2) {
        // YOUR CODE HERE
        _col1 = col1;
        _rel = relation;
        _col2 = col2;
    }

    /** A Condition representing COL1 RELATION 'VAL2', where COL1 is
     *  a column designator, VAL2 is a literal value (without the
     *  quotes), and RELATION is one of the strings "<", ">", "<=",
     *  ">=", "=", or "!=".
     */
    Condition(Column col1, String relation, String val2) {
        this(col1, relation, (Column) null);
        _val2 = val2;
    }

    /** Assuming that ROWS are rows from the respective tables from which
     *  my columns are selected, returns the result of performing the test I
     *  denote. */
    // This implementation of test(Row... rows) method :
    // only allows non-numeric type when check equility or inequility.
    // attempts applying >,<,>=,<= between non-numeric types will throw errors.
    boolean test(Row... rows) {
        // REPLACE WITH SOLUTION
        switch(_rel){
            case "<": // Any x < y iff !(Exist x >= y)
                try{
                    if(_col2 != null){
                        for(Row r: rows){
                            if(Double.valueOf(_col1.getFrom(r)) >= Double.valueOf(_col2.getFrom(r))){ // negate
                                return false;
                            }
                        }
                    } else {
                        for(Row r: rows){
                            if(Double.valueOf(_col1.getFrom(r)) >= Double.valueOf(_val2)){ // negate
                                return false;
                            }
                        }
                    }
                } catch (NumberFormatException e){
                    throw error("Exception: Unsupported comparison between non-numeric type");
                }
                return true;
            case ">": // Any x > y iff !(Exist x <= y)
                try{
                    if(_col2 != null){
                        for(Row r: rows){
                            if(Double.valueOf(_col1.getFrom(r)) <= Double.valueOf(_col2.getFrom(r))){ // negate
                                return false;
                            }
                        }
                    } else {
                        for(Row r: rows){
                            if(Double.valueOf(_col1.getFrom(r)) <= Double.valueOf(_val2)){ // negate
                                return false;
                            }
                        }
                    }
                } catch (NumberFormatException e){
                    throw error("Exception: Unsupported comparison between non-numeric type");
                }
                return true;
            case "<=": // Any x <= y iff !(Exist x > y)
                try{
                    if(_col2 != null){
                        for(Row r: rows){
                            if(Double.valueOf(_col1.getFrom(r)) > Double.valueOf(_col2.getFrom(r))){ // negate
                                return false;
                            }
                        }
                    } else {
                        for(Row r: rows){
                            if(Double.valueOf(_col1.getFrom(r)) > Double.valueOf(_val2)){ // negate
                                return false;
                            }
                        }
                    }
                } catch (NumberFormatException e){
                    throw error("Exception: Unsupported comparison between non-numeric type");
                }
                return true;
            case ">=": // Any x >= y iff !(Exist x < y)
                try{
                    if(_col2 != null){
                        for(Row r: rows){
                            if(Double.valueOf(_col1.getFrom(r)) < Double.valueOf(_col2.getFrom(r))){ // negate
                                return false;
                            }
                        }
                    } else {
                        for(Row r: rows){
                            if(Double.valueOf(_col1.getFrom(r)) < Double.valueOf(_val2)){ // negate
                                return false;
                            }
                        }
                    }
                } catch (NumberFormatException e){
                    throw error("Exception: Unsupported comparison between non-numeric type");
                }
                return true;
            case "=": // Any x == y iff !(Exist x != y)
                if(_col2 != null){
                    for(Row r: rows){
                        if(!(_col1.getFrom(r)).equals(_col2.getFrom(r))){ // negate
                            return false;
                        }
                    }
                } else {
                    for(Row r: rows){
                        if(!(_col1.getFrom(r)).equals(_val2)){ // negate
                            return false;
                        }
                    }
                }
                return true;
            case "!=": // Any x != y iff !(Exist x == y)
                if(_col2 != null){
                    for(Row r: rows){
                        if((_col1.getFrom(r)).equals(_col2.getFrom(r))){ // negate
                            return false;
                        }
                    }
                } else {
                    for(Row r: rows){
                        if((_col1.getFrom(r)).equals(_val2)){ // negate
                            return false;
                        }
                    }
                }
                return true;
            default:
                throw error("Exception: Unknown Relation: %s", _rel);
        }
    }

    /** Return true iff ROWS satisfies all CONDITIONS. */
    static boolean test(List<Condition> conditions, Row... rows) {
        for (Condition cond : conditions) {
            if (!cond.test(rows)) {
                return false;
            }
        }
        return true;
    }

    /** The operands of this condition.  _col2 is null if the second operand
     *  is a literal. */
    private Column _col1, _col2;
    /** Second operand, if literal (otherwise null). */
    private String _val2;
    // ADD ADDITIONAL FIELDS HERE
    /* This represents the relation */
    private String _rel;
}
