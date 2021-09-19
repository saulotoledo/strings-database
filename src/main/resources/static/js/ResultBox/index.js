import axios from 'axios';

import Search from "./js/Resultbox/Search";
import SearchResults from "./js/Resultbox/SearchResults";
import Pagination from "./js/Resultbox/Pagination";
import Alert from "./js/Alert";

const ResultBox = () => {
    const [ searchResult, setSearchResult ] = React.useState();
    const [ currentPage, setCurrentPage ] = React.useState(1);
    const [ paginationInfo, setPaginationInfo ] = React.useState();
    const [ isLoading, setIsLoading ] = React.useState(false);
    const [ error, setError ] = React.useState(undefined);
    const [ lastSearchValue, setLastSearchValue ] = React.useState('');

    const onSearchSubmit = (searchValue, page) => {
        setIsLoading(true);
        setLastSearchValue(searchValue);

        axios.get('/strings', {
            params: {
                filter: searchValue,
                sort: 'value',
                page: (page) ? page - 1 : 0, // <- First page in the API is 0
                size: 10, // <- Currently hardcoded, but it should be stored in a configuration
            }
        }).then((result) => {
            setIsLoading(false);
            if (result.status === 200) {
                setSearchResult(result.data.content);
                setPaginationInfo({
                    totalItems: result.data.totalElements,
                    perPage: result.data.size,
                });
            } else {
                setError(`Unexpected HTTP response code: ${result.status}`);
            }
        }, (reason) => {
            setError(reason.message);
            setIsLoading(false);
        });
    };

    const onPaginationChange = ({ current }) => {
        setCurrentPage(current);
        onSearchSubmit(lastSearchValue, current);
    };

    return (
        <div className="ResultBox row mb-5">
            <div className="col-lg-8 mx-auto">
                <h5 className="fw-light mb-4 font-italic text-white">Search strings in our database</h5>
                <div className="bg-white p-5 rounded shadow">

                    { error && (<Alert type="error" message={error} marginBottom={true} />) }
                    <Search onSearchSubmit={onSearchSubmit} isLoading={isLoading} />

                    { searchResult && searchResult.length > 0 && (
                        <p className="text-center mt-4">
                            <i className="bi-three-dots"></i>
                        </p>
                    ) }

                    <SearchResults values={searchResult} />

                    { paginationInfo && (
                        <div className="col-sm-12 mt-4 text-center">
                            <Pagination current={currentPage}
                                        onChange={onPaginationChange}
                                        totalItems={paginationInfo.totalItems}
                                        perPage={paginationInfo.perPage} />
                        </div>
                    ) }
                </div>
            </div>


        </div>
    );
}

export default ResultBox;
