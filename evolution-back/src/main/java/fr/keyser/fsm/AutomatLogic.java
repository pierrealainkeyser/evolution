package fr.keyser.fsm;

/**
 * The abstract functions of an automatic logic :
 * <ul>
 * <li>Find transition</li>
 * <li>Callback on leaving</li>
 * <li>Callbak on entering</li>
 * </ul>
 * 
 * @author pakeyser
 *
 */
public interface AutomatLogic {

	FollowedTransition findTransition(AutomatInstance instance, AutomatEvent event);

	AutomatInstance leaving(AutomatInstance leaving, State leaved, FollowedTransition transition);

	AutomatInstance entering(AutomatInstance entering, State entered, FollowedTransition transition);
}
