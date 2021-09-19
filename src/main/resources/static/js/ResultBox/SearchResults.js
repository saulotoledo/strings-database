import Alert from "./js/Alert";

const SearchResults = (props) => {
    if (!props.values) {
        return <React.Fragment></React.Fragment>;
    }

    let results;;
    const noResults = props.values.length == 0;

    if (!noResults) {
        results = props.values.map((stringEntry) => (
            <li key={stringEntry.id} className="list-group-item">
                {stringEntry.value}
            </li>
        ));
    }

    return (
        <div className="SearchResults row">
            { noResults && (<Alert type="info" message="No results found" marginTop={true} />) }
            { results && (<h6 className="mb-4">Search results:</h6>) }
            { results && (
                <ul className="list-group list-group-flush">
                    {results}
                </ul>
            ) }
        </div>
    );
}

export default SearchResults;
