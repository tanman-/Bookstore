import reqwest from 'reqwest'
import * as config from 'config'

class BookService {
	getLatestBook(offset, limit){
		return reqwest({
			url: config.getServerAddress() + '/book/latest',
			method: 'get',
			crossOrigin: true,
			data: {
				offset: offset,
				limit: limit
			}
		})
	}

	findFromGoogleAPI(isbn, title){
		let queries = []
		if (isbn) {
			queries.push(`isbn:${isbn}`)
		}
		if (title){
			queries.push(`title:${title}`)
		}
		//queries.push('maxResults=40')
		const queryString = queries.join("&")
		return reqwest({
			url: `https://www.googleapis.com/books/v1/volumes?q=${queryString}`,
			method: 'get',
			crossOrigin: true
		})
	}

	getBookDetail(isbn10, isbn13, title){
		return reqwest({
			url: config.getServerAddress() + '/book/',
			method: 'get',
			crossOrigin: true,
			data: {
				isbn10: isbn10,
				isbn13: isbn13,
				title: title
			}
		})
	}

	updateBookSale(salesData, book){
		let refinedSalesData = []
		salesData.forEach((sale) =>{
			refinedSalesData.push({
				quantity: sale.quantity,
				price: sale.price,
				bookCondition: sale.bookCondition
			})
		})

		book['sales'] = refinedSalesData

		return reqwest({
			url: config.getServerAddress() + '/book/sales/',
			method: 'put',
			crossOrigin: true,
			withCredentials: true,
			data: JSON.stringify(book),
			headers: {
				'Authorization': config.getAuthHeader(),
				'Content-Type': 'application/json'
			}
		})
	}

	getBookSales(salesRequest){
		return reqwest({
			url: config.getServerAddress() + '/book/sales/',
			method: 'post',
			crossOrigin: true,
			data: JSON.stringify(salesRequest),
			headers: {
				'Content-Type': 'application/json'
			}
		})
	}
}

export default BookService