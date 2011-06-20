package de.oldcastle.model {
	import mx.collections.ArrayCollection;
	
	
	[Bindable]
	public class WizardModelLocator{
		
		//Singleton specific
		private static var instance:WizardModelLocator;
		
		public function WizardModelLocator(enforcer:SingletonEnforcer){
			if (enforcer==null){
				throw new Error("There can only be one instance of ModelLocator.");	
			}
		}
		
		public static function getInstance():WizardModelLocator{
			if(instance==null){
				instance = new WizardModelLocator(new SingletonEnforcer);
			}
			
			return instance;
		}
		
		//Data & Manipulation
		//Content Editor - KMLInput
		public var fileName:String = "";
		public var authorName:String = "";
		public var authors:ArrayCollection = new ArrayCollection();
		public var kmlCode:String = "";
		
		public function addAuthor():void{
			if (authorName.length > 0){
					authors.addItem(authorName);
					authorName = "";
				}
		}
		public function remAuthor(index:int):void{
			authors.removeItemAt(index);
		}
		public function kmlToObjects():void{
			if(kmlCode.length > 0 && 
			kmlCode.substring(kmlCode.indexOf("<?"),kmlCode.indexOf("?>")+2) == 
			'<?xml version="1.0" encoding="UTF-8"?>'){
				
				kmlCode = kmlCode.substring(kmlCode.indexOf("<Document>"),kmlCode.length);
				kmlCode = "<kml>" + kmlCode;
				
				//TODO: TryCatch
				formattedKML = XML(kmlCode);
	//			for each(var pm:XML in formattedKML..Placemark){
	//			
	//			}
				contentEditor_wfState = CONTENT_EDITOR_EDITOBJECTS;
			}else{
				//Fehlerbehandlung
			}
		}
		
		//Content Editor - ObjectEditor
		public var formattedKML:XML = new XML();
		
		//Logic
		//Content Editor
		
		
		//WorkflowStates
		public var contentEditor_wfState:uint = CONTENT_EDITOR_CODEINPUT;
		public var contentSearch_wfState:uint = CONTENT_SEARCH_INIT;
		
		//WorkflowConstants
		public static const CONTENT_EDITOR_CODEINPUT:uint = 0;
		public static const CONTENT_EDITOR_EDITOBJECTS:uint = 1;
		public static const CONTENT_EDITOR_SENDINGDATA:uint = 2;
		public static const CONTENT_EDITOR_SENDINGOK:uint = 3;
		public static const CONTENT_EDITOR_ERROR:uint = 4;
		
		public static const CONTENT_SEARCH_INIT:uint = 0;
	}
}

class SingletonEnforcer{}