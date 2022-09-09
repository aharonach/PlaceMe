import {Outlet, useNavigate, useOutletContext, useParams} from "react-router-dom";
import {Alert, Button, ButtonGroup} from 'react-bootstrap';
import Loading from "../../Loading";
import {LinkContainer} from "react-router-bootstrap";
import useFetchRecord from "../../../hooks/useFetchRecord";

export default function ResultPage(){
    const { resultId } = useParams();
    const { placement, getPlacement } = useOutletContext();
    const baseUrl = `/placements/${placement.id}/results/`;
    const [result, error, loading, axiosFetch] = useFetchRecord({
        fetchUrl: baseUrl + resultId,
        displayFields: ['name']
    });
    const navigate = useNavigate();

    const handleDelete = () => {
        axiosFetch({
            method: 'delete',
            url: baseUrl + resultId
        }).then( () => getPlacement(), navigate(baseUrl, { replace: true }));
    }

    const makeSelected = () => {
        axiosFetch({
           method: 'post',
           url: baseUrl + 'selected',
           data: resultId
        }).then( res => res && getPlacement && getPlacement());
    }

    return (
        <>
            <Loading show={loading} />
            {!loading && error && <Alert variant="danger">{error}</Alert>}
            {!loading && result &&
                <article>
                    <div className="page-header">
                        <h2>{result.name}</h2>
                        <ButtonGroup>
                            <LinkContainer to={baseUrl + `${resultId}/edit`}><Button>Edit Result</Button></LinkContainer>
                            {result.selected ? '' : <Button variant="success" onClick={makeSelected}>Select Result</Button>}
                            <Button variant="danger" onClick={handleDelete}>Delete Result</Button>
                        </ButtonGroup>
                    </div>
                    <Outlet context={{ result, error, loading, axiosFetch }} />
                </article>
            }
        </>
    );
}