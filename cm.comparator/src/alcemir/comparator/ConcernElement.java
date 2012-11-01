package alcemir.comparator;

public class ConcernElement {
		private String degree;
		private String id;
		private String type;
		
		public ConcernElement(String degree, String id, String type) {
			this.degree = degree;
			this.id = id;
			this.type = type;
		}
		public String getDegree() {
			return degree;
		}
		public void setDegree(String degree) {
			this.degree = degree;
		}
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		
		@Override
		public String toString(){
			return "<element degree=\""+this.degree+"\" id=\""+this.id+"\" type=\""+this.type+"\"/>";
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((degree == null) ? 0 : degree.hashCode());
			result = prime * result + ((id == null) ? 0 : id.hashCode());
			result = prime * result + ((type == null) ? 0 : type.hashCode());
			return result;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ConcernElement other = (ConcernElement) obj;
			if (degree == null) {
				if (other.degree != null)
					return false;
			} else if (!degree.equals(other.degree))
				return false;
			if (id == null) {
				if (other.id != null)
					return false;
			} else if (!id.equals(other.id))
				return false;
			if (type == null) {
				if (other.type != null)
					return false;
			} else if (!type.equals(other.type))
				return false;
			return true;
		}
		
		
}
