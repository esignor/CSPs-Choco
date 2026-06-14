package codice.chocoCode.MapColoring;

import org.chocosolver.solver.exception.ContradictionException;

import org.chocosolver.solver.Solution;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.search.loop.lns.INeighborFactory;
import org.chocosolver.solver.search.limits.FailCounter;


/*value: 1 = red; 2 = blue, 3 = green
variables: WA; NT; SA; Q; NSW; V; T
*/

class mapColoring_local{
    public static void solve_mapColoring_local(){
        Model model = new Model("Map Coloring");
        IntVar[] ivars = new IntVar[7];

        IntVar WA = model.intVar("WA", 1, 3, false); // ogni stato puo' avere colore {blu, rosso, verde}
                                                         // false indica che i domini sono enumerati (non limitati)
        IntVar NT = model.intVar("NT", 1, 3, false);  
        IntVar SA = model.intVar("SA", 1, 3, false);
        IntVar Q = model.intVar("Q", 1, 3, false);
        IntVar NSW = model.intVar("NSW", 1, 3, false);
        IntVar V = model.intVar("V", 1, 3, false);
        IntVar T = model.intVar("T", 1, 3, false);
        ivars[0] = WA; ivars[1] = NT; ivars[2] = SA; ivars[3] = Q; ivars[4] = NSW; ivars[5] = V; ivars[6] = T;

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
        Solution solution = null;
        System.out.println("Strategia di ricerca LNS casuale \n");
        //soluzione che calcola una soluzione parziale basata su ivars.
        // Se non viene trovata alcuna soluzione entro 100 fail (FailCounter(solver, 100)), viene forzato un riavvio.
        solver.setLNS(INeighborFactory.random(ivars), new FailCounter(solver, 100));
        solution = solver.findOptimalSolution(WA, Model.MINIMIZE);
        solver.printStatistics();

        if(solution != null){
         System.out.println(solution.toString());
        }
    
        return;
    }

    // Le best soluzioni le ottengo con T, SA, NT

    public static void main (String[] args) throws ContradictionException {

            solve_mapColoring_local();

    }
                
}

