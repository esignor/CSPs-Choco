package codice.chocoCode.MapColoring;

import org.chocosolver.solver.exception.ContradictionException;

import org.chocosolver.solver.Solution;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;
import java.util.List;

/*value: 1 = red; 2 = blue, 3 = green
variables: WA; NT; SA; Q; NSW; V; T
*/

class mapColoring{

    public static void solve_mapColoring(){
        Model model = new Model("Map Coloring");

        IntVar WA = model.intVar("WA", 1, 3, false); // ogni stato puo' avere colore {blu, rosso, verde}
                                                         // false indica che i domini sono enumerati (non limitati)
        IntVar NT = model.intVar("NT", 1, 3, false);  
        IntVar SA = model.intVar("SA", 1, 3, false);
        IntVar Q = model.intVar("Q", 1, 3, false);
        IntVar NSW = model.intVar("NSW", 1, 3, false);
        IntVar V = model.intVar("V", 1, 3, false);
        model.intVar("T", 1, 3, false);

        //devo verificare che ogni stato abbia un colore diverso dai sui confinanti
        //dichiarazione dei vincoli
        WA.ne(NT).post(); WA.ne(SA).post();
        NT.ne(SA).post(); NT.ne(Q).post(); NT.ne(WA).post();
        SA.ne(NT).post(); SA.ne(WA).post(); SA.ne(NSW).post(); NSW.ne(Q).post(); SA.ne(V).post();
        Q.ne(NT).post(); Q.ne(SA).post(); Q.ne(NSW).post();
        NSW.ne(Q).post(); NSW.ne(SA).post(); NSW.ne(V).post();
        V.ne(NSW).post(); V.ne(SA).post();
        // T e' indipendente


        Solver solver = model.getSolver();
        List<Solution> solution = null;
        System.out.println("Strategia di ricerca predefinita defaultSearch \n");
        solution = solver.findAllSolutions(); // tutte le soluzioni lecite possibili
        solver.printStatistics();
        for(int i = 0; i < solution.size(); ++i) {
         System.out.println(solution.get(i).toString());
        }
        
    

        /* TEST PER TROVARE LA MIGIORE FRA LE SOLUZIONI
        System.out.println("Test \n");
        Solution solution_best = solver.findOptimalSolution(WA, Model.MINIMIZE);
        solver.printStatistics();
        if(solution_best != null){
           System.out.println(solution_best.toString());
        }*/
        
        
    

        return;
    }

    public static void main (String[] args) throws ContradictionException {

            solve_mapColoring();

    }
                
}

