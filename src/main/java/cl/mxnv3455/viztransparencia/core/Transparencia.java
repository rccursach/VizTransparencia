/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.mxnv3455.viztransparencia.core;
import cl.mxnv3455.viztransparencia.db.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.regex.Pattern;
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
        g.addAttribute("ui.stylesheet", "node.Caso { fill-color: #3333EE; }");
        g.addAttribute("ui.stylesheet", "node.Reclamante { fill-color: #33EE33; }");
        g.addAttribute("ui.stylesheet", "node.Reclamado { fill-color: #EE3333; }");
        g.addAttribute("ui.stylesheet", "node.Motivo { fill-color: #EE33EE; }");
        g.addAttribute("ui.stylesheet", "node.Estado { fill-color: #EEEE33; }");
        
        StatementResult res = db.ejecutarConsulta(query);
        db.close();
        
        while(res.hasNext()) {
            Record r = res.next();
            recordToGraph(r, g);
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
        
    private void recordToGraph(Record r, Graph g){
        Map m = r.asMap();
        Set s = m.keySet();
        Iterator it = s.iterator();
        ArrayList<Relationship> rels = new ArrayList();
        
        while(it.hasNext()) {
            Object o = m.get(it.next().toString());
            String cn = getClassName(o);
            if(cn.equals("InternalNode")) {
                Node node = (Node)o;
                try {
                    String label = getNodeLabel(node);
                    org.graphstream.graph.Node n = g.addNode(String.valueOf(node.id()));
                    n.addAttribute("ui.class", label);
                }
                catch(org.graphstream.graph.IdAlreadyInUseException e) {
                    System.err.println(e.getMessage());
                }
            }
            else if(cn.equals("InternalRelationship")) {
                Relationship rel = (Relationship)o;
                rels.add(rel);
            }
        }
        
        rels.forEach((rel) -> {
            try {
                String label = getRelLabel(rel);
                org.graphstream.graph.Edge e = g.addEdge(String.valueOf(rel.id()), String.valueOf(rel.startNodeId()), String.valueOf(rel.endNodeId()));
                e.addAttribute("ui.class", label);
            }
            catch(org.graphstream.graph.IdAlreadyInUseException | org.graphstream.graph.ElementNotFoundException e) {
                System.err.println(e.getMessage());
            }
        });
    }
    
    private String getClassName(Object o) {
        String long_cn = o.getClass().getName();
        String[] long_cn_tokenized = long_cn.split(Pattern.quote("."));
        String cn = long_cn_tokenized[long_cn_tokenized.length -1];
        return cn;
    }
    
    /**
     * ObtenerMotivos entrega una lista de motivos disponibles
     * @param consultaMotivos
     * @return 
     */
    
    public String[] obtenerMotivos(String consultaMotivos) {
        ArrayList<String> lista = new ArrayList<>();
        StatementResult res = db.ejecutarConsulta(consultaMotivos);
        int cont=0;
        while (res.hasNext()) {
            
            Record r = res.next();
            Node n = (Node) r.asMap().get("n");
            String l_n = "";
            
            try {
                l_n = (String)n.asMap().get("Motivo");
                lista.add(l_n);
            } catch (NoSuchElementException e) {
                System.err.println(e.getMessage());
            }
            cont++;
        }
        String[] respuesta= new String[cont];
        for (int i = 0; i < respuesta.length; i++) {
            respuesta[i]=lista.get(i);
            
        }
        return  respuesta;
    }
    
    /**
     * obtenerEstados entrega una lista de estados disponibles
     * @param consultaEstados
     * @return 
     */
    public String[] obtenerEstados(String consultaEstados) {
        ArrayList<String> lista = new ArrayList();
        StatementResult res = db.ejecutarConsulta(consultaEstados);
        int cont=0;
        while (res.hasNext()) {
            
            Record r = res.next();
            Node n = (Node) r.asMap().get("n");
            String l_n = "";
            try {
                l_n = (String)n.asMap().get("estado");
                lista.add(l_n);
            } catch (NoSuchElementException e) {
                System.err.println(e.getMessage());
            }
            cont++;
        }
        String[] respuesta= new String[cont];
        for (int i = 0; i < respuesta.length; i++) {
            respuesta[i]=lista.get(i);
            
        }
        return respuesta;
    }

}
