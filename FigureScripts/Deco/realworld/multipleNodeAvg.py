import os
import plotly.graph_objects as go
import plotly.io as pio
import plotly.express as px
import os
import sys
# Append parent directory to import path
sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
import config
pio.kaleido.scope.mathjax = None


fig = go.Figure()

fig.add_trace(go.Scatter(name="Central", x=[1, 2, 3, 4, 5], mode='lines+markers'
                         , y=[46494.372, 46494.372, 46494.372, 46494.372, 46494.372]
                         , line=dict(dash='dash', color=config.central, width=5), marker=dict(size=20, symbol='circle')))
fig.add_trace(go.Scatter(name="Disco", x=[1, 2, 3, 4, 5], mode='lines+markers'
                         , y=[471790.27, 471790.27, 471790.27, 471790.27, 471790.27]
                         , line=dict(dash='dashdot', color=config.disco, width=5), marker=dict(size=20, symbol='square')))
fig.add_trace(go.Scatter(name="Scotty", x=[1, 2, 3, 4, 5], mode='lines+markers'
                         , y=[3219430.076, 3219869.18, 3219108.58, 3206066.34, 3188692.59]
                         , line=dict(dash='dot', color=config.scotty, width=5), marker=dict(size=20, symbol='triangle-up')))
fig.add_trace(go.Scatter(name="Deco<sub>async</sub>", x=[1, 2, 3, 4, 5], mode='lines+markers'
                         , y=[4295481.287, 8589938.9551668, 12885449.285, 17159132.268, 21422494.87]
                         , line=dict(color=config.decoasy, width=5), marker=dict(size=20, symbol='cross')))

# fig.update_traces(marker_color='rgb(158,202,225)', marker_line_color='rgb(8,48,107)', marker_line_width=1.5, opacity=0.6,
# marker=dict(size=10, symbol='triangle-up'))))

#legend
fig.update_layout(
    legend=dict(
        orientation="h",
        yanchor="top",
        y=1.2,
        xanchor="left",
        x=-0.05,
        # bordercolor="Black",
        # borderwidth=2,
        # bgcolor="white",
        font=dict(
            size=30,
            color="black"
        ),
    ),
    yaxis=dict(
        title_text="Events/sec",
        titlefont=dict(size=35),
        exponentformat="e",
        ticktext=["0", "4M", "8M", "12M", "16M", "20M", "24M"],
        tickvals=[0, 4000000, 8000000, 12000000, 16000000, 20000000, 24000000],
        tickmode="array",
        range=[0, 25000000],
        tickfont=dict(size=35),
        # ticks="inside",
        # ticklen=20,
        # tickwidth =5,
    ),

    xaxis=dict(
        title_text="Local Nodes",
        titlefont=dict(size=35),
        ticktext=["1", "2", "3", "4", "5"],
        tickvals=[1, 2, 3, 4, 5],
        range=[1,5.2],
        tickmode="array",
        ticks="inside",
        ticklen=20,
        tickwidth=5,
        tickfont=dict(size=35),
    )
)

# size & color
fig.update_layout(
    autosize=False,
    width=660,
    height=440,
    paper_bgcolor='rgba(0,0,0,0)',
    plot_bgcolor='rgba(0,0,0,0)',
    margin=dict(
        l=5,
        r=5,
        b=5,
        t=5,
        pad=0
    ),
)
fig.update_xaxes(showline=True, linewidth=3, linecolor='black'#, mirror=True
                 )
fig.update_yaxes(showline=True, linewidth=3, linecolor='black'#, mirror=True
                 )
fig.update_yaxes(showgrid=True, gridwidth=1, gridcolor='rgb(120,120,120)')

fig.show()
pio.write_image(fig, "E:\On going Paper\Deco Efficient Decentralized Aggregation for Count-Based Windows\experiment\s3\/rsscalibility.pdf")
pio.write_image(fig, "E:\On going Paper\Deco Efficient Decentralized Aggregation for Count-Based Windows\experiment\s3\/rsscalibility.svg")