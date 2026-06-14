// PROGETTAZIONE DI VINCOLI

// Progettazione di vincoli piu' easy

/*sum_{i = 0 to n - 1} x_i <= limit where x_i = [inf(x_i), sup(x_i)] with distinct variables and limit is a const*/

package codice.chocoCode.ConstraintDesign;

import org.chocosolver.solver.constraints.Propagator;
import org.chocosolver.solver.constraints.PropagatorPriority;
import org.chocosolver.solver.exception.ContradictionException;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.events.IntEventType;
import org.chocosolver.util.ESat;

public class MyPropagator extends Propagator<IntVar> { 
    final int limit; //valore massimo che puo' assumere il vincolo di somma
    
    // costruttore
    public MyPropagator(IntVar[] x, int limit){
        super(x, PropagatorPriority.LINEAR, false);
        this.limit = limit;
    }

    // chimamata di eventi
    @Override
    public int getPropagationConditions(int vIdx){
        return IntEventType.combine(IntEventType.INSTANTIATE, IntEventType.INCLOW);
    }


    // propagate applica l'algoritmo di filtraggio: rimuove, dal suo dominio di variabili, valori che non possono appartenere a nessuna soluzione
    @Override
    public void propagate(int evtmask) throws ContradictionException{
        int sumLB = 0;
        for(int i = 0; i < vars.length; i++){
            sumLB +=  vars[i].getLB();
        }
        int F = limit - sumLB;
        if(F < 0){ // condizione di insoddisfazione soddisfatta
            fails();
        }
        for(int i = 0; i < vars.length; i++){
            int lb = vars[i].getLB();
            int ub = vars[i].getUB();
            if(ub - lb > F){ // con valori di UB si va al di fuori dei limiti imposti dal vincolo
                vars[i].updateUpperBound(F + lb, this); // aggiornamento di UB consistente con il vincolo
            }
        }
    }

   /* ESEMPIO DI FUNZIONAMENTO DI updateUpperBound
    [1,5] + [2,9] + [3,4] <= 10
   F = 10 - 6 = 4 > 0
   lb = 1   ub = 5
   ub - lb = 4  4 = F non faccio nulla, esempio: 5 + 2 + 3 = 10 ok
   ub - lb = 7 > 4  UB = 4 + 2 = 6, esempio: 1 + 9 + 3 = 13 > 10 non e' piu' valido post update: 1 + 6 + 3 = 10 ok
   ub - lb = 1 rimane valido non faccio nulla, esempio: 1 + 2 + 4 = 6 ok
   */

     // isEntailed stima se il propagatore e' gia' stato coinvolto nella ricerca degli assegnamenti alle variabili
    @Override
    public ESat isEntailed(){
        int sumUB = 0, sumLB = 0;
        for(int i = 0; i < vars.length; i++){
            sumLB += vars[i].getLB();
            sumUB += vars[i].getUB();   
        }
        if(sumUB <= limit){
            return ESat.TRUE;
        }

        if (sumLB > limit){
            return ESat.FALSE;
        }

        return ESat.UNDEFINED;
    }
}


