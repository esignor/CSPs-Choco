package codice.chocoCode.NQueens;

import org.chocosolver.solver.exception.ContradictionException;
import java.util.*;

import org.chocosolver.solver.Solution;
import org.chocosolver.solver.search.strategy.selectors.values.*;
import org.chocosolver.solver.search.strategy.selectors.variables.*;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.search.strategy.Search;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;

class nQueens{

    public static void solve_nQueens(int n, int type_search){
        Model model = new Model(n + "-queens problem");
        IntVar[] vars = new IntVar[n];
        // la variabili definisco le coordinate di una delle n regine sulla scacchiera
        // i = colonna
        // j = riga
        for(int i = 0; i < n; i++){
            vars[i] = model.intVar("Q_" + i, 1, n, false); // ogni coordinata puo' assumere valore da 1 a n, perche' ogni regina (una per riga) va assegnata a una colonna in modo da ridurre i conflitti
        }                                                  // false indica che i domini sono enumerati (non limitati)
         // fino a qui ho una sola regina per riga
         // ora devo verificare che non ci siano due regine che condividono la medesima colonna e diagonale
    
        for(int i = 0; i < n - 1; i++){ // mi fermo alla n - 1esima regina perche' vanno valutate a coppie
            for(int j = i + 1; j < n; j++){
                vars[i].ne(vars[j]).post(); //  se una regina i e' sulla colonna k allora la regina j non puo' anch'essa essere nella colonna k
                // la regina i e' nella colonna k, allora la regina i + 1 non puo' essere posizionata ne' nella colonna k + 1 e ne' k - 1
                // generalizzando i + p  non puo' venire assegnata a k + p
                vars[i].ne(vars[j].sub(j - i)).post();
                vars[i].ne(vars[j].add(j - i)).post();
            }
        }
        Solver solver = model.getSolver();
        Solution solution = null;
         
        if(type_search == 0){ // alcuna scelta della strategia di ricerca dal parte dell'utente
                              // la strategia di ricerca e' defaultSearch, che crea una ricerca di default per il modello.
                              // Questa euristica è completa (gestisce IntVar, BoolVar, SetVar e RealVar)
            System.out.println("Strategia di ricerca predefinita defaultSearch");
        }
    
    
        else if(type_search == 1){ // Strategia di assegnazione che seleziona una variabile secondo DomOverWDeg e la assegna al suo limite inferiore
                              // DomOverWDeg e' euristica di ordinamento delle variabili di grado ponderato che tiene conto delle dimensioni del dominio
            System.out.println("Strategia di ricerca domOverWDegRefSearch");
            solver.setSearch(Search.domOverWDegRefSearch(vars));
        }
    
    
        else if(type_search == 2){ // euristica di ricerca predefinita di variabili intere 
            System.out.println("Strategia di ricerca intVarSearch con FirstFail e IntDomainMiddle");
            solver.setSearch(Search.intVarSearch(new FirstFail(model), new IntDomainMiddle(false), vars)); /* politica di scelta delle variabili: FirstFail. 
                                                                                                            Sceglie la variabile con il dominio più piccolo
                                                                                                           politica di scelta del valore: IntDomainMiddle con politica ceiling. 
                                                                                                           Seleziona il valore nel dominio della variabile più vicino alla media
                                                                                                            dei suoi limiti attuali, arrottondando per eccesso*/
        }
    
    
    
        else if(type_search == 3){ //strategie di ricerca basata sulle attivita'
            System.out.println("Strategia di ricerca activityBasedSearch");
            solver.setSearch(Search.activityBasedSearch(vars));
        }

        else{
            System.out.println("Strategia inserita non esistente");
            return;

        }
        solution = solver.findSolution();
        solver.printStatistics();
        if(solution != null){
            System.out.println(solution.toString());
        }
    
        return;
    }

    // precondizione: numero di regine da inserire in scacchiera > 0

    public static void main (String[] args) throws ContradictionException {

            int n = 0, type_search = 0; 
            Scanner nScanner = null;
            Scanner solverScanner = null;

            try{
                System.out.print("Inserisci numero di regine (numero intero positivo): ");
                nScanner = new Scanner(System.in);
                n = nScanner.nextInt();
                System.out.print("Inserisci la strategia di ricerca che vuoi utilizzare: ");
                solverScanner = new Scanner(System.in);
                type_search = solverScanner.nextInt();
            }
            finally{
                if(nScanner != null && solverScanner != null){
                    nScanner.close(); solverScanner.close();
                }
            }
                

            System.out.println("Dati inseriti: " + n + " " + type_search + "\n");

            solve_nQueens(n, type_search);

    }
                
}

