const Pagination = props => {
    const getTotalPages = () => {
        const { totalItems, perPage } = props;
        return Math.ceil(totalItems / perPage);
    };

    const next = () => {
        const { perPage, current, onChange } = props;
        const total = getTotalPages();

        if (current < total) {
            const start = current * perPage;
            const end = (current + 1) * perPage;
            onChange && onChange({ start, end, current: current + 1 });
        }
    };

    const prev = () => {
        const { perPage, current, onChange } = props;
        const total = getTotalPages();

        if (current > 1 && current <= total) {
            const start = (current - 2) * perPage;
            const end = (current - 1) * perPage;
            onChange && onChange({ start, end, current: current - 1 });
        }
    };

    const direct = i => {
        const { perPage, onChange, current } = props;
        if (current !== i) {
            const start = (i - 1) * perPage;
            const end = i * perPage;
            onChange && onChange({ start, end, current: i });
        }
    };

    const { current } = props;
    const total = getTotalPages();

    let links = [];
    for (let i = 1; i <= total; i++) {
        links.push(
            <li key={i} className={`page-item${(current === i) ? ' active' : ''}`}>
                <button type="button"
                        className="btn btn-link page-link"
                        onClick={() => direct(i)}>
                    {i}
                </button>
            </li>
        );
    }

    return (
        <ul className="pagination justify-content-center">
            <li className={`page-item${(current === 1) ? ' disabled' : ''}`}>
                <button type="button"
                        className="btn btn-link page-link"
                        onClick={prev}
                >
                    Previous
                </button>
            </li>
            {links}
            <li className={`page-item${(current === total) ? ' disabled' : ''}`}>
                <button type="button"
                        className="btn btn-link page-link"
                        onClick={next}
                >
                    Next
                </button>
            </li>
        </ul>
    );
};

export default Pagination;
