package com.yahoo.ycsb.extensions.rdbms.model;


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name="properties")
public class XMLProperties {

	private Structural structure;
	private LoadContextual load;
	private BenchmarkContextual benchmark;

	@XmlElement(name="structure")
	public void setStructure(Structural structure){
		this.structure = structure;
	}
	
	@XmlElement(name="load")
	public void setLoad(LoadContextual load){
		this.load = load;
	}
	
	@XmlElement(name="benchmark")
	public void setBenchmark(BenchmarkContextual benchmark){
		this.benchmark = benchmark;
	}
	
	public Structural getStructure(){
		return this.structure;
	}
	
	public LoadContextual getLoad(){
		return this.load;
	}
	
	public BenchmarkContextual getBenchmark(){
		return this.benchmark;
	}
	
	/*
	private int operationCount;
	private ArrayList<Table> tables;
	private DiscreteGenerator tablechooser;

	public ArrayList<Table> getTables() {
		return this.tables;
	}

	public DiscreteGenerator getTableChooser(){
		return this.tablechooser;
	}
	
	@XmlElementWrapper(name="tables")
	@XmlElement(name="table")
	public void setTables(ArrayList<Table> _tables) throws WorkloadException{
		this.tablechooser = new DiscreteGenerator();
		for(int i = 0, l = _tables.size(); i < l; i++){
			this.tablechooser.addValue(_tables.get(i).getAccess(), i+"");
		}

		//TODO move all non XML Related actions to methods named setupX, setupY, ...
		for(Table table: _tables){
			ArrayList<Column> cols = table.getColumns();
			for(Column col: cols){
				col.getDistribution().setupDistribution(
					table.getKey().getRecordcount(), 
					table.getKey().getTransactioninsertkeysequence()
				);
			}

			if(table.getKey().getDistribution().isZipfianDistribution()){
				int expectednewkeys=(int)(this.operationCount*table.getAccess()*table.getProportions().getInsert()*2.0); //2 is fudge factor

				table.getKey().getDistribution().setGenerator(
					new ScrambledZipfianGenerator(table.getKey().getRecordcount()+expectednewkeys)
				);	
			}
		}

		this.tables = _tables;
	}

	public int getOperationCount() {
		return operationCount;
	}

	@XmlElement
	public void setOperationCount(int operationCount) {
		this.operationCount = operationCount;
	}

	//TODO change this to be clean
	public String toString(){
		String tos = "# of tables : "+tables.size()+"\n";
		int i = 0;
		for(Table table: tables){
			tos += "table["+i+"].name   : "+table.getName()+"\n"+
					"table["+i+"].access : "+table.getAccess()+"\n"+
					"table["+i+"].proportions.read   : "+table.getProportions().getRead()+"\n"+
					"table["+i+"].proportions.update : "+table.getProportions().getUpdate()+"\n"+
					"table["+i+"].proportions.insert : "+table.getProportions().getInsert()+"\n";
			ArrayList<Column> cols = table.getColumns();
			int j = 0;
			for(Column col: cols){
				tos += "table["+i+"].col["+j+"].name : "+col.getName()+"\n"+
						"table["+i+"].col["+j+"].type : "+col.getType()+"\n"+
						"table["+i+"].col["+j+"].distribution.type : "+col.getDistribution().getType()+"\n"+
						"table["+i+"].col["+j+"].distribution.min  : "+col.getDistribution().getMin()+"\n"+
						"table["+i+"].col["+j+"].distribution.max  : "+col.getDistribution().getMax()+"\n"+
						"table["+i+"].col["+j+"].distribution.scale: "+col.getDistribution().getScaling()+"\n"+
						"table["+i+"].col["+j+"].distribution.gen  : "+col.getDistribution().getGenerator();
				j++;
			}
			i++;
		}
		return tos;
	}
	
	*/
}
