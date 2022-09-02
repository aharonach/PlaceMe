import {Outlet, useNavigate, useParams} from "react-router-dom";
import {Alert, Button, ButtonGroup} from 'react-bootstrap';
import Loading from "../../Loading";
import {LinkContainer} from "react-router-bootstrap";
import useFetchRecord from "../../../hooks/useFetchRecord";

export default function ResultPage(){
    const { placementId } = useParams();
    let { resultId } = useParams();
    const baseUrl = `/placements/${placementId}/results/`;
    const [result, error, loading, axiosFetch] = useFetchRecord({
        fetchUrl: baseUrl + resultId,
        displayFields: ['name'],
    });
    const navigate = useNavigate();

    const handleDelete = () => {
        axiosFetch({
            method: 'delete',
            url: baseUrl + resultId
        }).then( res => res && navigate(baseUrl, { replace: true }));
    }

    const makeSelected = () => {
        axiosFetch({
           method: 'post',
           url: baseUrl + 'selected',
           data: resultId
        });
    }

    return (
        <>
            <Loading show={loading} />
            {!loading && error && <Alert variant="danger">{error}</Alert>}
            {!loading && result &&
                <article>
                    <h2>{result.name}</h2>
                    <ButtonGroup>
                        <LinkContainer to={baseUrl + `${resultId}/edit`}><Button>Edit Result</Button></LinkContainer>
                        {result.selected ? '' : <Button variant="success" onClick={makeSelected}>Select Result</Button>}
                        <Button variant="danger" onClick={handleDelete}>Delete Result</Button>
                    </ButtonGroup>
                    <Outlet context={{ result, error, loading, axiosFetch }} />
                </article>
            }
        </>
    );
}