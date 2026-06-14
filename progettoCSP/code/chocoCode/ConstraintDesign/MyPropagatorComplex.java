// PROGETTAZIONE DI VINCOLI

/* Progettazione di vincoli piu' complessa
modifiche rispetto a MyPropagator.java:
- calcolo di F in modo incrementale
*/

package codice.chocoCode.ConstraintDesign;

import org.chocosolver.memory.IStateInt;
import org.chocosolver.solver.constraints.Propagator;
import org.chocosolver.solver.constraints.PropagatorPriority;
import org.chocosolver.solver.exception.ContradictionException;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.events.IntEventType;
import org.chocosolver.solver.variables.events.PropagatorEventType;
import org.chocosolver.util.ESat;

public class MyPropagatorComplex extends Propagator<IntVar> { 
    final int limit; //valore massimo che puo' assumere il vincolo di somma
    final IStateInt F; // creazione di un backtrackable int che permette di registrare sia F che il LB delle variabili
    final IStateInt[] prev_lbs; // array che memorizza il precedente LB delle variabili
    
    // costruttore
    public MyPropagatorComplex(IntVar[] x, int limit){
        super(x, PropagatorPriority.LINEAR, false);
        this.limit = limit;
        this.F = this.model.getEnvironment().makeInt(0); // F e' inizializzato con valore 0
        this.prev_lbs = new IStateInt[x.length];
        for(int i = 0; i < x.length; i++){
            prev_lbs[i] = this.model.getEnvironment().makeInt(0); // prev_lbs inizializzato con valore 0
        }
    }

    // chimamata di eventi
    @Override
    public int getPropagationConditions(int vIdx){
        if(vIdx == -1){ // caso in cui non si e' ancora fatta alcuna modifica
            return IntEventType.combine(IntEventType.VOID);
        }
        else{ // evento di istanziazione o limite inferiore crescente
            return IntEventType.combine(IntEventType.INSTANTIATE, IntEventType.INCLOW);
        }
    }


    
    /* applicazione dell'algoritmo di filtraggio con reagisce agli eventi fini: incremento di F e filtraggio personalizzato */
      
                                                 
    private void prepare(){
        int sumLB = 0;
        for(int i = 0; i < vars.length; i++){
            sumLB += vars[i].getLB();
            prev_lbs[i].set(vars[i].getLB()); // contiene il valore corrente di LB per ogni variabile
        }
        F.set(limit - sumLB); // set del valore di F
    }

    @Override
    public void propagate(int vIdx, int evtmask) throws ContradictionException{
        int lb = vars[vIdx].getLB(); // LB della variabile modificata
        F.add(lb - prev_lbs[vIdx].get()); // viene aggiornato F con la differenza tra il vecchio e il nuovo LB
        prev_lbs[vIdx].set(lb); // viene impostato il nuovo LB
        forcePropagate(PropagatorEventType.CUSTOM_PROPAGATION); // il filtraggio viene delegato in un secondo momento (la rimozione dei valori dal dominio delle variabili)
    }
    
    @Override
    public void propagate(int evtmask) throws ContradictionException{
        if(PropagatorEventType.isFullPropagation(evtmask))
        // chiamata all'algoritmo di filtraggio, F non e' ancora aggiornato
        prepare(); // viene aggiornato F e impostato prev_lbs

        if(F.get() < 0){ // condizione di insoddisfazione soddisfatta
            fails();
        }
        for(IntVar var : vars) {
            int lb = var.getLB();
            int ub = var.getUB();
            if(ub - lb > F.get()){ // con valori di UB si va al di fuori dei limiti imposti dal vincolo
                var.updateUpperBound(F.get() + lb, this); // aggiornamento di UB consistente con il vincolo
            }
        }
    }

     // isEntailed stima se il propagatore e' gia' stato coinvolto nella ricerca degli assegnamenti alle variabili (rimane inalterata rispetto alla versione piu' semplice)
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


