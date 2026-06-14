package codice.chocoCode.ConstraintDesign;


import org.chocosolver.solver.Solution;
import org.chocosolver.solver.exception.ContradictionException;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.util.ESat;
import java.util.*;


class sum_problem {

    // precondizione: numero di variabili > 0
    public static void main (String[] args) throws ContradictionException {
        int n; int limit;
        Scanner nScanner = null; Scanner limitScanner = null;
        try{ 
            System.out.print("Inserisci numero di variabili del vincolo (numero intero positivo): ");
            nScanner = new Scanner(System.in);
            n = nScanner.nextInt();

            System.out.print("Inserisci limite del vincolo: ");
            limitScanner = new Scanner(System.in);
            limit = limitScanner.nextInt();
        }
        finally {
            if(nScanner!=null && limitScanner !=null)
                nScanner.close();
                limitScanner.close();
        }


        System.out.println("Il numero di variabili del vincolo e': " + n);
        System.out.println("Il limite del vincolo e': " + limit);

        Model model = new Model(n + "-sum problem");
        IntVar[] vars = new IntVar[n]; // alloco le variabili x_i

        for(int i = 0; i < n; i++ ){ // do un dominio a ogni variabile
        vars[i] = model.intVar("x" + i, 1, 100); // range valori di dominio [1,100]
        }
    
        MyPropagatorComplex  prop = new MyPropagatorComplex(vars, limit); // inizializzo il mio propagatore per dichiarare il vincolo personalizzato
        int event = prop.getPropagationConditions(-1); // sono all'inizio ancora nessun evento
        prop.propagate(event);
        try{
            for(int i = 0; i < n; i++){
            prop.propagate(i, event);
            event = prop.getPropagationConditions(i); // limite inferiore crescente
            prop.propagate(event);

        }
        if(prop.isEntailed() == ESat.TRUE){ // soluzione definita
            Solution solution = model.getSolver().findSolution();
            if(solution != null){
                System.out.println(solution.toString());
            }
        }
        else{ // soluzione indefinita
            System.out.println("La soluzione e' indefinita");   
        }
        }

        catch(ContradictionException e){ // non esiste soluzione valida
            System.out.println("Non esiste alcuna combinazione di domini di variabili tale che il vincolo '<= " + limit + "' " + "possa venire rispettato");
        }

    }
    }


