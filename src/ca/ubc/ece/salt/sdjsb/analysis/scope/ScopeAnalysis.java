package ca.ubc.ece.salt.sdjsb.analysis.scope;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.ScriptNode;

import ca.ubc.ece.salt.sdjsb.alert.Alert;
import ca.ubc.ece.salt.sdjsb.analysis.Analysis;
import ca.ubc.ece.salt.sdjsb.analysis.flow.FunctionTreeVisitor;
import ca.ubc.ece.salt.sdjsb.cfg.CFG;

/**
 * Builds a scope tree for the source and destination ASTs. This is used
 * as the basis for performing flow analysis, but can also be used stand-alone
 * if only scope is needed.
 */
public class ScopeAnalysis implements Analysis {

	/**
	 * The repair alerts generated by the analysis.
	 */
	private Map<AstNode, List<Alert>> alerts;
	
	protected List<CFG> srcCFGs;
	protected List<CFG> dstCFGs;
	
	protected Scope srcScope;
	protected Scope dstScope;
	
	/** Maps function nodes to their scopes. */
	protected Map<ScriptNode, Scope> srcScopeMap;

	/** Maps function nodes to their scopes. */
	protected Map<ScriptNode, Scope> dstScopeMap;
	
	public ScopeAnalysis() {
		this.alerts = new HashMap<AstNode, List<Alert>>();
	}
	
	/**
	 * @return the source scope tree.
	 */
	public Scope getSrcScope() {
		return this.srcScope;
	}
	
	/**
	 * @return the destination scope tree.
	 */
	public Scope getDstScope() {
		return this.dstScope;
	}
	
	/**
	 * @param node the script or function
	 * @return the source scope tree.
	 */
	public Scope getSrcScope(ScriptNode node) {
		return this.srcScopeMap.get(node);
	}

	/**
	 * @param node the script or function
	 * @return the destination scope tree.
	 */
	public Scope getDstScope(ScriptNode node) {
		return this.dstScopeMap.get(node);
	}

	@Override
	public void analyze(AstRoot root, List<CFG> cfgs) throws Exception {

		this.dstScopeMap = new HashMap<ScriptNode, Scope>();
		this.dstCFGs = cfgs;
		this.dstScope = this.buildScopeTree(root, null, this.dstScopeMap);

	}

	@Override
	public void analyze(AstRoot srcRoot, List<CFG> srcCFGs, AstRoot dstRoot,
			List<CFG> dstCFGs) throws Exception {

		this.srcScopeMap = new HashMap<ScriptNode, Scope>();
		this.dstScopeMap = new HashMap<ScriptNode, Scope>();
		this.srcCFGs = srcCFGs;
		this.dstCFGs = dstCFGs;
		this.srcScope = this.buildScopeTree(srcRoot, null, this.srcScopeMap);
		this.dstScope = this.buildScopeTree(dstRoot, null, this.dstScopeMap);

	}

	/**
	 * Builds the scope tree. 
	 * @return the root of the scope tree.
	 * @throws Exception
	 */
	private Scope buildScopeTree(ScriptNode function, Scope parent, Map<ScriptNode, Scope> scopeMap) throws Exception {
		
        /* Create a new scope for this script or function and add it to the 
         * scope tree. */
        
		Scope scope = new Scope(parent, function);
		if(parent != null) parent.children.add(scope);
		ScopeVisitor.getLocalScope(scope);
		
		/* Put the scope in the scope map. */
		
		scopeMap.put(function, scope);
        
        /* Analyze the methods of the function. */

        List<FunctionNode> methods = FunctionTreeVisitor.getFunctions(function);
        for(FunctionNode method : methods) {
        	buildScopeTree(method, scope, scopeMap);
        }
        
        return scope;
        
	}
	
	/**
	 * Registers an alert to be reported to the user.
	 * @param alert
	 */
	protected void registerAlert(AstNode node, Alert alert) {
		List<Alert> alerts = this.alerts.get(node);
		
		if(alerts == null) {
			alerts = new LinkedList<Alert>();
			this.alerts.put(node, alerts);
		}
		
		alerts.add(alert);
	}

	/**
	 * @return a list of the alerts from this analysis.
	 */
	@Override
	public Set<Alert> getAlerts() {
		Set<Alert> alerts = new HashSet<Alert>();
		for(AstNode node : this.alerts.keySet()){
			alerts.addAll(this.alerts.get(node));
		}
		return alerts;
	}

}
