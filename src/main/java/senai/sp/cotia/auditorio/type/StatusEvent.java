package senai.sp.cotia.auditorio.type;



public enum StatusEvent {
	CONFIRMADO("Confirmado"),
	ANALISE("Analise"),
	FINALIZADO("Finalizado");
	
	String status;
	
	private StatusEvent(String status) {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	
}
