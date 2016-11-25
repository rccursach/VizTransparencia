/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.mxnv3455.viztransparencia.core;
import cl.mxnv3455.viztransparencia.db.*;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.types.Node;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.types.Relationship;
/**
 *
 * @author rccursach
 */
public class Transparencia {
    
    private final Neo4j db;

    public Transparencia(String db_usr, String db_pass) {
        this.db = new Neo4j(db_usr, db_pass);
    }
    
    public void makeGraphFromQuery(String query) {
        Graph g = new SingleGraph("grafoCasos");
        StatementResult res = db.ejecutarConsulta(query);
        db.close();
        
        while(res.hasNext()) {
            Record r = res.next();
            Node n = (Node)r.asMap().get("n");
            Relationship re = (Relationship)r.asMap().get("r");
            Node o = (Node)r.asMap().get("o");
            
            String l_n = getNodeLabel(n);
            //String l_re = getNodeLabel(re);
            String l_o = getNodeLabel(o);
            
            System.out.println(l_n);
            System.out.println(r.asMap());
            //System.out.println(n.asMap());
            //System.out.println(n.labels().);
            try {
                g.addNode(String.valueOf(n.id()));
                g.addNode(String.valueOf(o.id()));
                g.addEdge(String.valueOf(re.id()), String.valueOf(n.id()), String.valueOf(o.id()));
            }
            catch(org.graphstream.graph.IdAlreadyInUseException e) {
                System.err.println(e.getMessage());
            }
        }
        
        g.display();
    }
    
    private String getNodeLabel(Node n) {
        Iterator it_n = n.labels().iterator();
        String l_n = "";
        try {
         l_n = (String)it_n.next();
        }
        catch(NoSuchElementException e){
            System.err.println(e.getMessage());
        }
        return l_n;
    }
    
    private String getRelLabel(Relationship n) {
        return n.type();
    }
}
