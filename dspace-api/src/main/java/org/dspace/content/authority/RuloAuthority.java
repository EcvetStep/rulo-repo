package org.dspace.content.authority;

import org.dspace.core.Context;
import org.dspace.storage.rdbms.DatabaseManager;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.dspace.storage.rdbms.TableRow;

/**
 * Created by tkout on 7/9/15.
 */
public class RuloAuthority implements ChoiceAuthority {
    private static Logger log = Logger.getLogger(RuloAuthority.class);

    private String values[] = null;
    private String labels[] = null;


    /**
     * For invoking via the command line.
     * Run with the dsrun command:
     * ./bin/dspace rulos
     */
    public static void main(String[] argv) throws Exception {
        System.out.println("Partially implemented, beware!");
        RuloAuthority ra = new RuloAuthority();

        Choices ch = ra.getMatches("field", "query", 0, 0, 10, "en");
        for (int i = 0; i < ch.total; i++) {
            Choice cv = ch.values[i];
            System.out.println(cv.label + ": " + cv.value);
        }
    }

    // constructor
    public RuloAuthority() {
        log.info("Created new RULO Authority");
    }


    // once-only load of values and labels
    private void init() {
        if (values == null) {
            try {

                Context context = new Context();
                String query = "SELECT item.item_id,handle.handle, metadatavalue.text_value FROM public.item,public.handle,public.metadatavalue WHERE item.item_id = handle.resource_id AND metadatavalue.resource_id = item.item_id AND metadata_field_id = 64;";

                List<TableRow> res = DatabaseManager.query(context, query).toList();
                values = new String[res.size()];
                labels = new String[res.size()];
                TableRow tr;
                for (int i = 0; i < res.size(); i++) {
                    tr = res.get(i);
                    values[i] = String.valueOf(tr.getIntColumn("item_id"));
                    labels[i] = tr.getStringColumn("handle") + " -- "
                            + tr.getStringColumn("text_value");
                }

                log.debug("Initialized Rulos Authority with " + res.size() + " rulos");
            } catch (SQLException se) {
                log.error("Cannot init!", se);
            }
        }
    }

    /**
     * Get all values from the authority that match the preferred value.
     * Note that the offering was entered by the user and may contain
     * mixed/incorrect case, whitespace, etc so the plugin should be careful
     * to clean up user data before making comparisons.
     * <p/>
     * While we have a small set of values of RULOS we simply return the whole
     * set for any sample value, although it's a good idea to set the
     * defaultSelected index in the Choices instance to the choice, if any,
     * that matches the value.
     *
     * @param field      being matched for
     * @param query      user's value to match
     * @param collection database ID of Collection for context (owner of Item)
     * @param start      choice at which to start, 0 is first.
     * @param limit      maximum number of choices to return, 0 for no limit.
     * @param locale     explicit localization key if available, or null
     * @return a Choices object (never null).
     */
    @Override public Choices getMatches(
            String field, String query, int collection, int start, int limit, String locale)
    {
        init();

        int dflt = -1;
        Choice v[] = new Choice[values.length];
        log.info("Trying to get matches!");
        for (int i = 0; i < values.length; ++i) {
            v[i] = new Choice(values[i], values[i], labels[i]);
            if (values[i].equalsIgnoreCase(query)) {
                dflt = i;
            }
        }
        log.info("Returning: " + Arrays.toString(v));
        return new Choices(v, 0, v.length, Choices.CF_AMBIGUOUS, false, dflt);
    }

    /**
     * Get the single "best" match (if any) of a value in the authority
     * to the given user value.  The "confidence" element of Choices is
     * expected to be set to a meaningful value about the circumstances of
     * this match.
     * <p/>
     * This call is typically used in non-interactive metadata ingest
     * where there is no interactive agent to choose from among options.
     *
     * @param field      being matched for
     * @param text       user's value to match
     * @param collection database ID of Collection for context (owner of Item)
     * @param locale     explicit localization key if available, or null
     * @return a Choices object (never null) with 1 or 0 values.
     */
    @Override public Choices getBestMatch(
            String field, String text, int collection, String locale)
    {
        init();
        for (int i = 0; i < values.length; ++i) {
            if (text.equalsIgnoreCase(values[i])) {
                Choice v[] = new Choice[1];
                v[0] = new Choice(String.valueOf(i), values[i], labels[i]);
                return new Choices(v, 0, v.length, Choices.CF_UNCERTAIN, false, 0);
            }
        }
        return new Choices(Choices.CF_NOTFOUND);
    }

    /**
     * Get the canonical user-visible "label" (i.e. short descriptive text)
     * for a key in the authority.  Can be localized given the implicit
     * or explicit locale specification.
     * <p/>
     * This may get called many times while populating a Web page so it should
     * be implemented as efficiently as possible.
     *
     * @param field  being matched for
     * @param key    authority key known to this authority.
     * @param locale explicit localization key if available, or null
     * @return descriptive label - should always return something, never null.
     */
    @Override public String getLabel(String field, String key, String locale) {
        init();
        return labels[Integer.parseInt(key)];
    }


}
