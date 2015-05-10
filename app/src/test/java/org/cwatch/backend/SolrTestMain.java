package org.cwatch.backend;

import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.core.CoreContainer;
import org.apache.solr.core.CoreDescriptor;
import org.apache.solr.core.NodeConfig.NodeConfigBuilder;
import org.apache.solr.core.SolrConfig;
import org.apache.solr.core.SolrCore;
import org.apache.solr.core.SolrResourceLoader;
import org.apache.solr.core.SolrXmlConfig;
import org.apache.solr.schema.IndexSchema;
import org.xml.sax.InputSource;

import com.google.common.collect.ImmutableList;

public class SolrTestMain {
	
	public static void main(String[] args) throws Exception {
		SolrConfig config = new SolrConfig();
		IndexSchema is = new IndexSchema(config, "solrTestIndexSchema", new InputSource(SolrTestMain.class.getResourceAsStream("/solrschema.xml")));
		CoreContainer coreContainer = new CoreContainer(SolrXmlConfig.fromConfig(config)) {
			{
				//SolrCore core = new SolrCore("solrTestCore", new CoreDescriptor(this, "solrTestCore", "target/solr/core"));
				SolrCore core = new SolrCore("solrTestCore", "target/solr/data", config, is, new CoreDescriptor(this, "solrTestCore", "target/solr/core"));
				registerCore("solrTestCore", core, false);
			}
		};
		//SolrCore core = new SolrCore("solrTestCore", "target/solr/core/data", config, is, new CoreDescriptor(coreContainer, "solrTestcore", "target/solr/core"));
		EmbeddedSolrServer server = new EmbeddedSolrServer(coreContainer, "solrTestCore");
		
		SolrInputDocument doc1 = new SolrInputDocument();
	    doc1.addField( "id", "id1", 1.0f );
	    doc1.addField( "name", "doc1", 1.0f );
	    doc1.addField( "price", 10 );
	    
	    server.add(ImmutableList.of(doc1));
	}

}
