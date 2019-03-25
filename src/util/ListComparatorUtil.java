package util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 结果比较
 *
 */
@SuppressWarnings("rawtypes")
public class ListComparatorUtil {

	
	/**
	 * @param lst1 来源数据
	 * @param lst2  本数据源
	 * @return	List[], 数据结构说明：[新增数据集合(ArrayList<Map>),更新数据集合(ArrayList<Map>)]
	 */
	public static final List[] compare(List<Map> lst1,List<Map> lst2) {
		List[] result = new List[2];
		
		ArrayList<Map> add_result = new ArrayList<Map>();//add
		result[0] = add_result;

		ArrayList<Map> update_result = new ArrayList<Map>();//update
		result[1] = update_result;
		
		if(lst1 != null && !lst1.isEmpty()
				&& lst2 != null && !lst2.isEmpty()){
			//将List<Map>转化为Map<String,Map>
			Map<String,Map> map1 = new HashMap<String,Map>();
			for(Map m1:lst1){
				String id = m1.get("id").toString();
				map1.put(id, m1);
			}
			
			Map<String,Map> map2 = new HashMap<String,Map>();
			for(Map m1:lst2){
				String id = m1.get("id").toString();
				map2.put(id, m1);
			}
			
			//map1和map2对比
			Set<String> keySet = map1.keySet();
			for (String key : keySet) {
				Map m1 = map1.get(key);
				if(map2.containsKey(key)){//存在数据
					Map m2 = map2.get(key);
					
					Set m_keySet = m1.keySet();
					boolean isSame = true;
					for (Object m_key : m_keySet) {
						String o1 = m1.get(m_key)+"";
						String o2 = m2.get(m_key)+"";
						if(o1.compareTo(o2) != 0){
							isSame = false;
							break;
						}
					}
					if(!isSame){
						update_result.add(m1);
					}
				}else{//不存在数据，该数据属于新增数据
					add_result.add(m1);
				}
			}
		}else{
			if(lst1 != null && !lst1.isEmpty()){
				add_result.addAll(lst1);
			}
			if(lst2 != null && !lst2.isEmpty()){//不合理情况
			}
		}
		return result;
	}
}
