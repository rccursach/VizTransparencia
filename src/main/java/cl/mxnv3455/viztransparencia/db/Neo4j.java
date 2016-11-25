/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.mxnv3455.viztransparencia.db;
import org.neo4j.driver.v1.*;
/**
 *
 * @author rccursach
 */
public class Neo4j {
    private Driver driver;
    private Session session;

    public Neo4j(String user, String pass) {
        this.driver = GraphDatabase.driver("bolt://localhost", AuthTokens.basic(user, pass));
        this.session = driver.session();
    }
    public StatementResult ejecutarConsulta(String consulta){
        try {
            return session.run(consulta);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
    }
    
    public void close(){
        this.session.close();
        this.driver.close();
    }
    
}

