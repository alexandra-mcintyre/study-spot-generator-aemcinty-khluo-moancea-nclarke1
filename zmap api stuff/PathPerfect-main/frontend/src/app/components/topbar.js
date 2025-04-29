/**
 * This is the `Topbar` component, which represents the top navigation bar of your application.
 * It includes the application name and a GitHub link button.
 *
 * @returns {JSX.Element} The JSX element representing the `Topbar` component.
 */
const Topbar = () => {
  return (
    <header className="w-full flex justify-between items-center flex-col">
      <nav className="flex justify-between items-center w-full mb-10 pt-3">
        <p className="w-28" style={{ padding: "15px", marginLeft: "15px", fontSize: "20px" }}>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css"></link>
        <i className="fa fa-circle" style={{fontSize: "20px", color: "#0096FF"}}></i>
        &nbsp;  PathPerfect
        </p>

        <button
          type="button"
          onClick={() => window.open('https://github.com/Eric-04/PathPerfect')}
          className="nav_icon" style={{ position: "absolute", top: "15px", right: "25px" }}>
          <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css"></link>
          <i className="fa fa-github" style={{ fontSize: "36px"}}></i>
        </button>
      </nav>
    </header>
  );
}

export default Topbar;
