package com.jovision.commons;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 类似稀松数组的集合，实现了基本 Map 和 List 功能
 * 
 * @author neo
 * 
 * @param <E>
 */
public class MyList<E> {

	private static final int DEFAULT_START_INDEX = 0;

	private HashMap<Integer, E> map;

	private int startIndex = DEFAULT_START_INDEX;

	/**
	 * 构造，默认从 0 开始的索引
	 */
	public MyList() {
		map = new HashMap<Integer, E>();
	}

	/**
	 * 通过指定开始索引的构造
	 * 
	 * @param startIndex
	 */
	public MyList(int startIndex) {
		this.startIndex = startIndex;
		map = new HashMap<Integer, E>();
	}

	/**
	 * 调试用
	 */
	public void echo() {
		System.out.println(toString() + "-----");
	}

	/**
	 * 交换两个元素位置
	 * 
	 * @param org
	 *            原始元素索引
	 * @param dst
	 *            目标元素索引
	 * @return 交换是否成功
	 */
	public boolean swap(int org, int dst) {
		boolean result = false;
		if (hasIndex(org) && hasIndex(dst)) {
			Integer orgInteger = Integer.valueOf(org);
			Integer dstInteger = Integer.valueOf(dst);

			// [Neo] classic way
			// E tmp = map.get(orgInteger);
			// map.put(orgInteger, map.get(dstInteger));
			// map.put(dstInteger, tmp);

			if (null != map.put(dstInteger,
					map.put(orgInteger, map.get(dstInteger)))) {
				result = true;
			}
		}
		return result;
	}

	/**
	 * 移除指定索引的记录
	 * 
	 * @param index
	 * @return 移除是否成功
	 */
	public boolean remove(int index) {
		boolean result = false;
		if (hasIndex(index)) {
			if (null != map.remove(Integer.valueOf(index))) {
				result = true;
			}
		}
		return result;
	}

	/**
	 * 移除指定元素的记录
	 * 
	 * @param e
	 * @return 移除是否成功
	 */
	public boolean remove(E e) {
		boolean result = false;
		if (hasElement(e)) {
			Iterator<Map.Entry<Integer, E>> iterator = (Iterator<Map.Entry<Integer, E>>) map
					.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry<Integer, E> entry = (Map.Entry<Integer, E>) iterator
						.next();
				Integer key = (Integer) entry.getKey();
				E value = (E) map.get(key);
				if (value.equals(e)) {
					if (null != map.remove(key)) {
						result = true;
					}
					break;
				}
			}
		}
		return result;
	}

	/**
	 * 预先检查默认添加元素的索引
	 * 
	 * @return
	 */
	public int precheck() {
		int current = startIndex;
		Iterator<Map.Entry<Integer, E>> iterator = (Iterator<Map.Entry<Integer, E>>) map
				.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<Integer, E> entry = (Map.Entry<Integer, E>) iterator
					.next();
			Integer key = (Integer) entry.getKey();
			if (key.intValue() > current) {
				break;
			}
			current++;
		}
		return current;
	}

	/**
	 * 添加元素，自动计算稀松位置
	 * 
	 * @param e
	 * @return 添加是否成功
	 */
	public boolean add(E e) {
		return add(precheck(), e);
	}

	/**
	 * 为指定索引添加元素，会覆盖已存在的索引记录
	 * 
	 * @param index
	 * @param e
	 * @return 添加是否成功
	 */
	public boolean add(int index, E e) {
		boolean result = false;
		if (null != map.put(Integer.valueOf(index), e)) {
			result = true;
		}
		return result;
	}

	/**
	 * 清理记录
	 * 
	 */
	public void clear() {
		map.clear();
	}

	/**
	 * 检查是否有指定的索引记录
	 * 
	 * @param index
	 * @return
	 */
	public boolean hasIndex(int index) {
		return map.containsKey(Integer.valueOf(index));
	}

	/**
	 * 检查是否有指定的元素记录
	 * 
	 * @param e
	 * @return
	 */
	public boolean hasElement(E e) {
		return map.containsValue(e);
	}

	/**
	 * 通过索引获取元素
	 * 
	 * @param index
	 * @return
	 */
	public E get(int index) {
		return map.get(Integer.valueOf(index));
	}

	/**
	 * 判断是否为空
	 * 
	 * @return
	 */
	public boolean isEmpty() {
		return map.isEmpty();
	}

	/**
	 * 获取当前集合的记录个数
	 * 
	 * @return
	 */
	public int size() {
		return map.size();
	}

	@Override
	public String toString() {
		StringBuilder sBuilder = new StringBuilder();
		Iterator<Map.Entry<Integer, E>> iterator = (Iterator<Map.Entry<Integer, E>>) map
				.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<Integer, E> entry = (Map.Entry<Integer, E>) iterator
					.next();
			Integer key = (Integer) entry.getKey();
			E value = (E) map.get(key);
			sBuilder.append(key).append(" => ").append(value).append("\n");
		}
		return sBuilder.toString();
	}

	/**
	 * 输出一个有序列表集合
	 * 
	 * @return
	 */
	public ArrayList<E> toList() {
		ArrayList<Map.Entry<Integer, E>> list = new ArrayList<Map.Entry<Integer, E>>(
				map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<Integer, E>>() {

			@Override
			public int compare(Map.Entry<Integer, E> o1,
					Map.Entry<Integer, E> o2) {
				return o1.getKey().intValue() - o2.getKey().intValue();
			}
		});

		ArrayList<E> result = new ArrayList<E>();
		for (Map.Entry<Integer, E> entry : list) {
			result.add(entry.getValue());
		}
		return result;
	}

}
