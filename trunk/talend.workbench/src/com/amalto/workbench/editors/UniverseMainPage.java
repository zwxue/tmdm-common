package com.amalto.workbench.editors;

import java.io.StringWriter;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.exolab.castor.xml.Marshaller;

import com.amalto.workbench.models.KeyValue;
import com.amalto.workbench.models.Line;
import com.amalto.workbench.providers.XObjectEditorInput;
import com.amalto.workbench.utils.FontUtils;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.webservices.WSGetObjectsForUniverses;
import com.amalto.workbench.webservices.WSUniverse;
import com.amalto.workbench.webservices.WSUniverseItemsRevisionIDs;
import com.amalto.workbench.webservices.WSUniverseXtentisObjectsRevisionIDs;
import com.amalto.workbench.webservices.XtentisPort;
import com.amalto.workbench.widgets.ComplexTableViewer;
import com.amalto.workbench.widgets.LabelText;

public class UniverseMainPage extends AMainPageV2{

	//protected Text nameText;
	protected Text descriptionText;
	protected DropTarget windowTarget;

	protected TableViewer instancesViewer;

	protected Universe universe;
	
	protected Text reversionIDText;
	protected FormToolkit toolkit;
	
	private String[] columns=new String[]{"Concept Name pattern","Revision ID"};
	
	protected Map<String,LabelText> xtentisObjectsLabelTexts=new HashMap<String,LabelText>();
	private LabelText defaultReversionIDText;
	
	public UniverseMainPage(FormEditor editor) {
        super(
        		editor,
        		UniverseMainPage.class.getName(),
        		"Universe "+((XObjectEditorInput)editor.getEditorInput()).getName()
        );        
	}

	@Override
	protected void createCharacteristicsContent(FormToolkit toolkit,
			Composite charComposite) {
	try {
		if(universe==null)universe=new Universe("");
		this.toolkit=toolkit;


        //description
        Label descriptionLabel = toolkit.createLabel(charComposite, "Description", SWT.NULL);
        descriptionLabel.setLayoutData(
                new GridData(SWT.FILL,SWT.CENTER,false,true,1,1)
        );
        descriptionText = toolkit.createText(charComposite, "",SWT.BORDER|SWT.MULTI);
        descriptionText.setLayoutData(    
                new GridData(SWT.FILL,SWT.FILL,true,true,1,1)
        );
        ((GridData)descriptionText.getLayoutData()).minimumHeight = 30;
        descriptionText.addModifyListener(new ModifyListener() {
        	public void modifyText(ModifyEvent e) {
        		if (refreshing) return;
        		universe.setDescription(descriptionText.getText());
        		markDirty();
        	}
        }); 
        
        //make the Page window a DropTarget - we need to dispose it
        windowTarget = new DropTarget(this.getPartControl(), DND.DROP_MOVE);
        windowTarget.setTransfer(new Transfer[]{TextTransfer.getInstance()});
        windowTarget.addDropListener(new DCDropTargetListener());
        
        //Items Section          
        Composite itemsGroup = this.getNewSectionComposite("Concept Revision ID");
        itemsGroup.setLayout(new GridLayout(2,false));
		defaultReversionIDText =new LabelText(toolkit,itemsGroup,"Default Revision ID");       
		defaultReversionIDText.getText().addModifyListener(new ModifyListener() {
        	public void modifyText(ModifyEvent e) {
        		if (refreshing) return;
        		universe.setDefaultReversionID(defaultReversionIDText.getText().getText().trim());
        		markDirty();
        	}
        });
        Composite itemsComposite = toolkit.createComposite(itemsGroup, SWT.BORDER);
        itemsComposite.setLayoutData(
                new GridData(SWT.FILL,SWT.FILL,true,true,2,1)
        );       
        itemsComposite.setLayout(new GridLayout(1,true));
        
        ComplexTableViewer itemsViewer=new ComplexTableViewer(Arrays.asList(columns),toolkit,itemsComposite);
        itemsViewer.create();
        itemsViewer.setMainPage(this);
        instancesViewer=itemsViewer.getViewer();
        instancesViewer.setInput(universe.getItemsList());                    
        
        //Xtentis Objects  Section
        Composite objecstGroup = this.getNewSectionComposite("Xtentis Objects Revision ID");
        objecstGroup.setLayout(new GridLayout(1,true));
        
        Composite objectsComposite = toolkit.createComposite(objecstGroup, SWT.BORDER);
        objectsComposite.setLayoutData(
                new GridData(SWT.FILL,SWT.FILL,true,true,1,1)
        );
        objectsComposite.setLayout(new GridLayout(2,false));

        for(KeyValue line: universe.getXtentisObjectsList()){
        	createLabelText(objectsComposite, line.key);
        }
        
        refreshData();

    } catch (Exception e) {
        e.printStackTrace();
    }
		
	}
	protected void createLabelText(Composite parent, final String labelName){
		final LabelText labelText =new LabelText(toolkit,parent,labelName);
		labelText.getLabel().setFont(FontUtils.getBoldFont(labelText.getLabel().getFont()));
		labelText.getText().addModifyListener(new ModifyListener() {
        	public void modifyText(ModifyEvent e) {
        		if (refreshing) return;
        		KeyValue line=getKeyValue(labelName);
        		if(line!=null)line.value=labelText.getText().getText();
        		markDirty();
        	}
        });
		xtentisObjectsLabelTexts.put(labelText.getLabel().getText(),labelText);
	}
	private KeyValue getKeyValue(String name){
		for(KeyValue line: universe.getXtentisObjectsList()){
			if(name.equals(line.key)){
				return line;
			}
		}
		return null;
	}
	@Override
	protected void createActions() {
		// TODO Auto-generated method stub
		
	}

	protected void refreshData() {
		try {

			if (this.comitting) return;
			
			this.refreshing = true;
			
			WSUniverse wsUniverse = (WSUniverse) (getXObject().getWsObject());    	
			
			universe = new Universe("");
			universe.setName(wsUniverse.getName());
			universe.setDefaultReversionID(wsUniverse.getDefaultItemsRevisionID());
			universe.setDescription(wsUniverse.getDescription()==null ? "" : wsUniverse.getDescription());
			universe.setDefaultReversionID(wsUniverse.getDefaultItemsRevisionID());
			universe.getXtentisObjectsList().clear();
			for(WSUniverseXtentisObjectsRevisionIDs xtentisObjects: wsUniverse.getXtentisObjectsRevisionIDs()){
				universe.getXtentisObjectsList().add(new KeyValue(xtentisObjects.getXtentisObjectName(),xtentisObjects.getRevisionID()));
			}
			
			for(WSUniverseItemsRevisionIDs item:wsUniverse.getItemsRevisionIDs()){
				universe.getItemsList().add(new Line(columns,new String[]{item.getConceptPattern(),item.getRevisionID()}));
			}
			
			XtentisPort port = Util.getPort(getXObject());
            String[] objects = port.getObjectsForRoles(null).getStrings();
            Arrays.sort(objects);
	    	
			//Now fill in the values on the page
            descriptionText.setText(universe.getDescription()==null ? "" : universe.getDescription());
            defaultReversionIDText.getText().setText(universe.getDefaultReversionID()==null?"":universe.getDefaultReversionID());
            //nameText.setText(universe.getName()==null?"":universe.getName())  ;
            for(KeyValue line: universe.getXtentisObjectsList()){
            	LabelText labelText=xtentisObjectsLabelTexts.get(line.key);
            	if(labelText!=null){
            		labelText.getText().setText(line.value);
            	}
            }
            instancesViewer.setInput(universe.getItemsList());
            this.refreshing = false;

		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(this.getSite().getShell(), "Error refreshing the page", "Error refreshing the page: "+e.getLocalizedMessage());
		}    	
	}
	
	protected void commit() {
		try {
			if (this.refreshing) return;
			
			this.comitting = true;
			
			WSUniverse ws = (WSUniverse) (getXObject().getWsObject());    	
			ws.setName(universe.getName());
			ws.setDefaultItemsRevisionID(universe.getDefaultReversionID());
			ws.setDescription(universe.getDescription());
			List<WSUniverseXtentisObjectsRevisionIDs> xtentisObjectsRevisionIDs=new ArrayList<WSUniverseXtentisObjectsRevisionIDs>();
			for(KeyValue line: universe.getXtentisObjectsList()){
				xtentisObjectsRevisionIDs.add(new WSUniverseXtentisObjectsRevisionIDs(line.key,line.value));
			}
			ws.setXtentisObjectsRevisionIDs(xtentisObjectsRevisionIDs.toArray(new WSUniverseXtentisObjectsRevisionIDs[xtentisObjectsRevisionIDs.size()] ));
			List<WSUniverseItemsRevisionIDs> itemIds=new ArrayList<WSUniverseItemsRevisionIDs>();
			for(Line line: universe.getItemsList()){				
				itemIds.add(new WSUniverseItemsRevisionIDs(line.keyValues.get(0).value,line.keyValues.get(1).value));
			}
			ws.setItemsRevisionIDs(itemIds.toArray(new WSUniverseItemsRevisionIDs[itemIds.size()]));
			this.comitting = false;
			
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(this.getSite().getShell(), "Error comtiting the page", "Error comitting the page: "+e.getLocalizedMessage());
		}    	
	}

	/****************************************************************************
	 *   Comptroler Model
	 ****************************************************************************/
	public class Universe {
		protected String name=null;
	    protected String description=null;
	    protected String defaultReversionID;
	    protected List<KeyValue> xtentisObjectsList=new ArrayList<KeyValue>();
	    protected List<Line> itemsList=new ArrayList<Line>();
	    public Universe(String defaultReversionID) throws RemoteException{
	    	this.defaultReversionID=defaultReversionID;
	    	for(String str: getPort().getObjectsForUniverses(new WSGetObjectsForUniverses(new String[]{".*"})).getStrings()){//IConstants.XTENTISOBJECTS){
	    		xtentisObjectsList.add(new KeyValue(str,""));
	    	}
	    }
	    
		public String getDefaultReversionID() {
			return defaultReversionID;
		}

		public void setDefaultReversionID(String defaultReversionID) {
			this.defaultReversionID = defaultReversionID;
		}

		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}

		public List<KeyValue> getXtentisObjectsList() {
			return xtentisObjectsList;
		}

		public void setXtentisObjectsList(List<KeyValue> xtentisObjectsList) {
			this.xtentisObjectsList = xtentisObjectsList;
		}

		public List<Line> getItemsList() {
			return itemsList;
		}

		public void setItemsList(List<Line> itemsList) {
			this.itemsList = itemsList;
		}

		@Override
		public String toString() {
			StringWriter sw = new StringWriter();
			try {
				Marshaller.marshal(this, sw);
			} catch (Exception e) {
				System.out.println("ERROR marshalling Role");
				e.printStackTrace();
			}
			return sw.toString();
		}
	
	}//class Universe
	
	


}
