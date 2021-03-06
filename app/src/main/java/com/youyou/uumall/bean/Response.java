/*
 * Copyright (C) 2007 The Android  Source Project
 *
 * Licensed under the ZenMall License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.zenmall.com/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.youyou.uumall.bean;

public class Response<T> {
	public Integer code;
	public String msg;
	public Integer size;
	public T data;
	public Double totalPrice;
	public Integer prev;
	public Integer next;

	@Override
	public String toString() {
		return "Response{" +
				"code=" + code +
				", msg='" + msg + '\'' +
				", size=" + size +
				", data=" + data +
				", totalPrice=" + totalPrice +
				", prev=" + prev +
				", next=" + next +
				'}';
	}
}
