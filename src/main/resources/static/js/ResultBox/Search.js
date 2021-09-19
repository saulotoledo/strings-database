const Search = (props) => {
    const [ searchValue, setSearchValue ] = React.useState('');

    const onSubmit = (event) => {
        event.preventDefault();
        props.onSearchSubmit(searchValue);
    };

    return (
        <form onSubmit={onSubmit}>
            <div className="input-group">
                <input type="search"
                       placeholder="What are you searching for?"
                       aria-describedby="button-search"
                       className="form-control shadow-none"
                       disabled={props.isLoading}
                       value={searchValue}
                       onChange={(event) => setSearchValue(event.target.value)}
                />
                <button id="button-search"
                        type="submit"
                        className="btn btn-primary shadow-none"
                        disabled={props.isLoading}
                >
                    { props.isLoading && <div className="spinner-border spinner-border-sm text-light me-2" role="status"></div> }
                    { props.isLoading ? 'Searching...' : <i className="bi-search"></i> }

                </button>
            </div>
        </form>
    );
}

export default Search;
