package org.dspace.content.authority;

import org.dspace.core.Context;
import org.dspace.storage.rdbms.DatabaseManager;
import org.dspace.storage.rdbms.TableRow;
import org.dspace.storage.rdbms.TableRowIterator;

import java.sql.SQLException;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;

/**
 * Created by tkout on 7/9/15.
 */
public class TKTestAuthority implements ChoiceAuthority  {

    /** The RULOs that exist in the database */
    private final HashMap rulos;

    /** DSpace Context object */
    private final Context context;
    
    
    /**
     * For invoking via the command line.
     * Run with the dsrun command:
     * ./bin/dspace tkta
     */
    public static void main(String[] argv) throws Exception {

        System.out.println("[TKTestAuthority] Starting at " + (new GregorianCalendar().getTime().toString()) );

        // the sql query
        String query = "SELECT item.item_id,handle.handle, metadatavalue.text_value FROM public.item,public.handle,public.metadatavalue WHERE item.item_id = handle.resource_id AND metadatavalue.resource_id = item.item_id AND metadata_field_id = 64;";

        // create context as super user
        try {
            TKTestAuthority tkta = new TKTestAuthority();
            System.out.println("[TKTestAuthority] Context is " + tkta.context.toString());

//            TableRow row = DatabaseManager.querySingle(tkta.context, query);
            
            TableRowIterator tri = DatabaseManager.query(tkta.context, query);

            System.out.println("Results ----------------------------------------------------- ");

            TableRow tr;
            String key;
            while (tri.hasNext()) {
                tr = tri.next();
                key = tr.getStringColumn("handle");
                tkta.rulos.put(key, tr.getStringColumn("handle") + " -- " + tr.getStringColumn("text_value"));

                System.out.println(tkta.rulos.get(key));
            }
            
        } catch (SQLException se) {
            System.out.println("Failed to create context" + se.toString());
            System.exit(0);
        }
    }

    /**
     * constructor, which just creates and object with a ready context
     *
     * @throws SQLException
     */
    public TKTestAuthority() throws SQLException {
        context = new Context();
        rulos = new HashMap();
    }
    

    public Choices getMatches(String field, String query, int collection, int start, int limit, String locale) {
        int dflt = -1;
        String[] values = (String[]) this.rulos.values().toArray();
        String[] labels = (String[]) this.rulos.keySet().toArray();
        
        Choice v[] = new Choice[values.length];
        for (int i = 0; i < values.length; ++i)
        {
            v[i] = new Choice(String.valueOf(i), values[i], labels[i]);
            if (values[i].equalsIgnoreCase(query))
            {
                dflt = i;
            }
        }
        return new Choices(v, 0, v.length, Choices.CF_AMBIGUOUS, false, dflt);
    }

    public Choices getBestMatch(String field, String text, int collection, String locale) {
        String[] values = (String[]) this.rulos.values().toArray();
        String[] labels = (String[]) this.rulos.keySet().toArray();

        for (int i = 0; i < values.length; ++i)
        {
            if (text.equalsIgnoreCase(values[i]))
            {
                Choice v[] = new Choice[1];
                v[0] = new Choice(String.valueOf(i), values[i], labels[i]);
                return new Choices(v, 0, v.length, Choices.CF_UNCERTAIN, false, 0);
            }
        }
        return new Choices(Choices.CF_NOTFOUND);
    }

    public String getLabel(String field, String key, String locale) {
        return (String) rulos.get(key);
    }
    
}
