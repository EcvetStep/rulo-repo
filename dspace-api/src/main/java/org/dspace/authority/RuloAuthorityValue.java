/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.authority;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Tasos K (anastasios.koutoumanos at gmail dot com)
 */
public class RuloAuthorityValue extends AuthorityValue {

    private String identifier;
    private String title;
    private List<String> nameVariants = new ArrayList<String>();
    private String institution;

    public RuloAuthorityValue() {
    }

    public RuloAuthorityValue(SolrDocument document) {
        super(document);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String t) {
        if (!StringUtils.equals(getValue(), t)) {
            setValue(t);
        }
    }

    @Override
    public void setValue(String value) {
        super.setValue(value);
        setTitle(value);
    }

    public List<String> getNameVariants() {
        return nameVariants;
    }

    public void addNameVariant(String name) {
        if (StringUtils.isNotBlank(name)) {
            nameVariants.add(name);
        }
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    @Override
    public SolrInputDocument getSolrInputDocument() {
        SolrInputDocument doc = super.getSolrInputDocument();
        if (StringUtils.isNotBlank(getTitle())) {
            doc.addField("title", getTitle());
        }
        for (String nameVariant : getNameVariants()) {
            doc.addField("name_variant", nameVariant);
        }
        doc.addField("institution", getInstitution());
        return doc;
    }

    @Override
    public void setValues(SolrDocument document) {
        super.setValues(document);
        this.title = ObjectUtils.toString(document.getFieldValue("title"));
        nameVariants = new ArrayList<String>();
        Collection<Object> document_name_variant = document.getFieldValues("name_variant");
        if (document_name_variant != null) {
            for (Object name_variants : document_name_variant) {
                addNameVariant(String.valueOf(name_variants));
            }
        }
        if (document.getFieldValue("institution") != null) {
            this.institution = String.valueOf(document.getFieldValue("institution"));
        }
    }


    @Override
    public Map<String, String> choiceSelectMap() {

        Map<String, String> map = super.choiceSelectMap();

        if (StringUtils.isNotBlank(getTitle())) {
            map.put("title", getTitle());
        } else {
            map.put("title", "/");
        }
        if (StringUtils.isNotBlank(getInstitution())) {
            map.put("institution", getInstitution());
        }

        return map;
    }

    @Override
    public String getAuthorityType() {
        return "rulo";
    }

    @Override
    public String generateString() {
        return AuthorityValueGenerator.GENERATE + getAuthorityType() + AuthorityValueGenerator.SPLIT + getTitle();
        // the part after "AuthorityValueGenerator.GENERATE + getAuthorityType() + AuthorityValueGenerator.SPLIT" is the value of the "info" parameter in public AuthorityValue newInstance(String info)
    }

    @Override
    public AuthorityValue newInstance(String info) {
        RuloAuthorityValue authorityValue = new RuloAuthorityValue();
        authorityValue.setValue(info);
        return authorityValue;
    }

    @Override
    public String toString() {
        return "RuloAuthorityValue{" +
                "title='" + title + '\'' +
                ", nameVariants=" + nameVariants +
                ", institution='" + institution + '\'' +
                "} " + super.toString();
    }

    public boolean hasTheSameInformationAs(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if(!super.hasTheSameInformationAs(o)){
            return false;
        }

        RuloAuthorityValue that = (RuloAuthorityValue) o;

        if (title != null ? !title.equals(that.title) : that.title != null) {
            return false;
        }
        if (institution != null ? !institution.equals(that.institution) : that.institution != null) {
            return false;
        }
        if (nameVariants != null ? !nameVariants.equals(that.nameVariants) : that.nameVariants != null) {
            return false;
        }

        return true;
    }
}
